package com.socialscreencontrol.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.socialscreencontrol.core.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeGroups(userId: String): Flow<List<AccountabilityGroup>> = callbackFlow {
        val listener = firestore.collection("groups")
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot.toObjects())
            }
        awaitClose { listener.remove() }
    }

    suspend fun createGroup(group: AccountabilityGroup) {
        firestore.collection("groups").document(group.id).set(group).await()
    }

    fun observeChat(groupId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("groups").document(groupId).collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects() ?: emptyList()) }
        awaitClose { listener.remove() }
    }

    suspend fun addChatMessage(groupId: String, message: ChatMessage) {
        firestore.collection("groups").document(groupId).collection("messages")
            .document(message.id).set(message).await()
    }

    fun observeLimits(userId: String): Flow<List<AppLimit>> = callbackFlow {
        val listener = firestore.collection("users").document(userId).collection("limits")
            .addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects() ?: emptyList()) }
        awaitClose { listener.remove() }
    }

    suspend fun upsertLimit(userId: String, limit: AppLimit) {
        firestore.collection("users").document(userId).collection("limits")
            .document(limit.packageName).set(limit).await()
    }

    suspend fun saveUsage(snapshot: UsageSnapshot) {
        val key = "${snapshot.dateKey}_${snapshot.packageName}"
        firestore.collection("users").document(snapshot.userId).collection("usage")
            .document(key).set(snapshot).await()
    }

    fun observeUsageToday(userId: String, dateKey: String): Flow<List<UsageSnapshot>> = callbackFlow {
        val listener = firestore.collection("users").document(userId).collection("usage")
            .whereEqualTo("dateKey", dateKey)
            .addSnapshotListener { snapshot, _ -> trySend(snapshot?.toObjects() ?: emptyList()) }
        awaitClose { listener.remove() }
    }

    suspend fun createRequest(request: TimeRequest): String {
        val doc = firestore.collection("groups").document(request.groupId)
            .collection("requests").document()
        doc.set(request.copy(id = doc.id)).await()
        return doc.id
    }

    fun observeRequests(groupId: String): Flow<List<TimeRequest>> = callbackFlow {
        val listener = firestore.collection("groups").document(groupId).collection("requests")
            .whereEqualTo("status", RequestStatus.PENDING.name)
            .addSnapshotListener { snapshot, _ ->
                val values = snapshot?.documents?.mapNotNull { doc ->
                    val raw = doc.data ?: return@mapNotNull null
                    TimeRequest(
                        id = doc.id,
                        groupId = raw["groupId"] as? String ?: "",
                        requesterId = raw["requesterId"] as? String ?: "",
                        packageName = raw["packageName"] as? String ?: "",
                        extraMinutes = (raw["extraMinutes"] as? Long)?.toInt() ?: 0,
                        reason = raw["reason"] as? String ?: "",
                        status = RequestStatus.valueOf(raw["status"] as? String ?: RequestStatus.PENDING.name)
                    )
                } ?: emptyList()
                trySend(values)
            }
        awaitClose { listener.remove() }
    }

    suspend fun vote(groupId: String, requestId: String, vote: Vote, memberCount: Int): TimeRequest {
        val ref = firestore.collection("groups").document(groupId).collection("requests").document(requestId)
        firestore.runTransaction { tx ->
            val snap = tx.get(ref)
            val existingVotes = (snap["votes"] as? List<Map<String, Any>>).orEmpty()
            val withoutUser = existingVotes.filterNot { it["userId"] == vote.userId }
            val merged = withoutUser + mapOf(
                "userId" to vote.userId,
                "decision" to vote.decision.name,
                "comment" to vote.comment,
                "votedAt" to FieldValue.serverTimestamp()
            )

            val approves = merged.count { it["decision"] == VoteType.APPROVE.name }
            val rejects = merged.count { it["decision"] == VoteType.REJECT.name }
            val majority = (memberCount / 2) + 1
            val status = when {
                approves >= majority -> RequestStatus.APPROVED.name
                rejects >= majority -> RequestStatus.REJECTED.name
                else -> RequestStatus.PENDING.name
            }
            tx.update(ref, mapOf("votes" to merged, "status" to status))
        }.await()

        return ref.get().await().toObject<TimeRequest>()?.copy(id = requestId)
            ?: throw IllegalStateException("Request not found")
    }

    suspend fun findUsersByPhones(phoneNumbers: List<String>): List<UserProfile> {
        if (phoneNumbers.isEmpty()) return emptyList()
        return firestore.collection("users")
            .whereIn("phoneNumber", phoneNumbers.take(10))
            .get().await().toObjects()
    }
}
