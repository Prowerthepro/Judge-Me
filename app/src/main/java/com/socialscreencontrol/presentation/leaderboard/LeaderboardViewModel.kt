package com.socialscreencontrol.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.core.model.LeaderboardEntry
import com.socialscreencontrol.domain.repository.GroupRepository
import com.socialscreencontrol.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    groupRepository: GroupRepository,
    usageRepository: UsageRepository
) : ViewModel() {
    private val uid = auth.currentUser?.uid.orEmpty()
    private val selectedGroup = MutableStateFlow<String?>(null)

    val entries: StateFlow<List<LeaderboardEntry>> = selectedGroup.flatMapLatest { groupId ->
        if (groupId == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else combine(groupRepository.observeGroups(uid), usageRepository.observeUsageToday(uid)) { groups, usage ->
            val memberIds = groups.firstOrNull { it.id == groupId }?.memberIds.orEmpty()
            memberIds.mapIndexed { index, member ->
                LeaderboardEntry(userId = member, userName = member, totalMinutesToday = usage.sumOf { it.usedMinutes }, rank = index + 1)
            }.sortedBy { it.totalMinutesToday }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun bindGroup(groupId: String) { selectedGroup.value = groupId }
}
