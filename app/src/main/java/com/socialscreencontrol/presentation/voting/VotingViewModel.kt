package com.socialscreencontrol.presentation.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.core.model.TimeRequest
import com.socialscreencontrol.core.model.Vote
import com.socialscreencontrol.core.model.VoteType
import com.socialscreencontrol.domain.repository.RequestRepository
import com.socialscreencontrol.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VotingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val requestRepository: RequestRepository,
    private val usageRepository: UsageRepository
) : ViewModel() {

    private val currentGroupId = MutableStateFlow("")

    val requests: StateFlow<List<TimeRequest>> = currentGroupId.flatMapLatest { groupId ->
        if (groupId.isBlank()) kotlinx.coroutines.flow.flowOf(emptyList()) else requestRepository.observePendingRequests(groupId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun bindGroup(groupId: String) { currentGroupId.value = groupId }

    fun vote(request: TimeRequest, approve: Boolean, comment: String) = viewModelScope.launch {
        val vote = Vote(
            userId = auth.currentUser?.uid.orEmpty(),
            decision = if (approve) VoteType.APPROVE else VoteType.REJECT,
            comment = comment,
            votedAt = Timestamp.now()
        )
        val updated = requestRepository.voteOnRequest(request.groupId, request.id, vote)
        if (updated.status.name == "APPROVED") {
            usageRepository.grantExtraTime(updated.requesterId, updated.packageName, updated.extraMinutes)
        }
    }
}
