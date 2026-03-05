package com.socialscreencontrol.domain.repository

import com.socialscreencontrol.core.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthUser(): Flow<UserProfile?>
    suspend fun requestPhoneAuth(phone: String)
    suspend fun verifyOtp(verificationId: String, code: String)
    suspend fun createOrUpdateProfile(name: String)
}

interface GroupRepository {
    fun observeGroups(userId: String): Flow<List<AccountabilityGroup>>
    suspend fun createGroup(name: String, members: List<String>)
    suspend fun sendChatMessage(groupId: String, content: String)
    fun observeGroupChat(groupId: String): Flow<List<ChatMessage>>
    suspend fun discoverUsersByPhones(phoneNumbers: List<String>): List<UserProfile>
}

interface UsageRepository {
    fun observeLimits(userId: String): Flow<List<AppLimit>>
    suspend fun upsertLimit(userId: String, limit: AppLimit)
    suspend fun saveUsageSnapshot(snapshot: UsageSnapshot)
    fun observeUsageToday(userId: String): Flow<List<UsageSnapshot>>
    suspend fun grantExtraTime(userId: String, packageName: String, minutes: Int)
}

interface RequestRepository {
    fun observePendingRequests(groupId: String): Flow<List<TimeRequest>>
    suspend fun createRequest(request: TimeRequest): String
    suspend fun voteOnRequest(groupId: String, requestId: String, vote: Vote): TimeRequest
}
