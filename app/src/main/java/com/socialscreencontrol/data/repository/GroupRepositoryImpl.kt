package com.socialscreencontrol.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.socialscreencontrol.core.model.AccountabilityGroup
import com.socialscreencontrol.core.model.ChatMessage
import com.socialscreencontrol.core.model.UserProfile
import com.socialscreencontrol.data.remote.FirebaseFirestoreService
import com.socialscreencontrol.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreService: FirebaseFirestoreService
) : GroupRepository {
    override fun observeGroups(userId: String): Flow<List<AccountabilityGroup>> =
        firestoreService.observeGroups(userId)

    override suspend fun createGroup(name: String, members: List<String>) {
        val owner = auth.currentUser?.uid ?: return
        val group = AccountabilityGroup(
            id = UUID.randomUUID().toString(),
            name = name,
            ownerId = owner,
            memberIds = (members + owner).distinct(),
            createdAt = Timestamp.now()
        )
        firestoreService.createGroup(group)
    }

    override suspend fun sendChatMessage(groupId: String, content: String) {
        val senderId = auth.currentUser?.uid ?: return
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            senderId = senderId,
            content = content,
            createdAt = Timestamp.now()
        )
        firestoreService.addChatMessage(groupId, message)
    }

    override fun observeGroupChat(groupId: String): Flow<List<ChatMessage>> =
        firestoreService.observeChat(groupId)

    override suspend fun discoverUsersByPhones(phoneNumbers: List<String>): List<UserProfile> =
        firestoreService.findUsersByPhones(phoneNumbers)
}
