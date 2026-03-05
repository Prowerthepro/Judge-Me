package com.socialscreencontrol.core.model

import com.google.firebase.Timestamp

data class UserProfile(
    val id: String = "",
    val phoneNumber: String = "",
    val displayName: String = "",
    val avatarUrl: String? = null,
    val contactsHash: List<String> = emptyList(),
    val fcmToken: String = ""
)

data class AccountabilityGroup(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val memberIds: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)

data class AppLimit(
    val packageName: String = "",
    val appName: String = "",
    val dailyLimitMinutes: Int = 120,
    val extraGrantedMinutes: Int = 0,
    val enabled: Boolean = true
)

data class UsageSnapshot(
    val userId: String = "",
    val packageName: String = "",
    val dateKey: String = "",
    val usedMinutes: Int = 0,
    val updatedAt: Timestamp = Timestamp.now()
)

enum class RequestStatus { PENDING, APPROVED, REJECTED }
enum class VoteType { APPROVE, REJECT }

data class Vote(
    val userId: String = "",
    val decision: VoteType = VoteType.REJECT,
    val comment: String = "",
    val votedAt: Timestamp = Timestamp.now()
)

data class TimeRequest(
    val id: String = "",
    val groupId: String = "",
    val requesterId: String = "",
    val packageName: String = "",
    val extraMinutes: Int = 15,
    val reason: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val votes: List<Vote> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val resolvedAt: Timestamp? = null
)

data class ChatMessage(
    val id: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

data class LeaderboardEntry(
    val userId: String = "",
    val userName: String = "",
    val totalMinutesToday: Int = 0,
    val rank: Int = 0
)

data class UserStreak(
    val userId: String = "",
    val currentDays: Int = 0,
    val longestDays: Int = 0,
    val lastValidDateKey: String = ""
)
