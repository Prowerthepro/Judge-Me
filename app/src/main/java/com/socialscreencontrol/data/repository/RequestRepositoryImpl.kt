package com.socialscreencontrol.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.socialscreencontrol.core.model.TimeRequest
import com.socialscreencontrol.core.model.Vote
import com.socialscreencontrol.data.remote.FirebaseFirestoreService
import com.socialscreencontrol.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RequestRepositoryImpl @Inject constructor(
    private val firestoreService: FirebaseFirestoreService,
    private val firestore: FirebaseFirestore
) : RequestRepository {
    override fun observePendingRequests(groupId: String): Flow<List<TimeRequest>> =
        firestoreService.observeRequests(groupId)

    override suspend fun createRequest(request: TimeRequest): String =
        firestoreService.createRequest(request)

    override suspend fun voteOnRequest(groupId: String, requestId: String, vote: Vote): TimeRequest {
        val members = firestore.collection("groups").document(groupId).get().await()
            .get("memberIds") as? List<*> ?: emptyList<String>()
        return firestoreService.vote(groupId, requestId, vote, members.size)
    }
}
