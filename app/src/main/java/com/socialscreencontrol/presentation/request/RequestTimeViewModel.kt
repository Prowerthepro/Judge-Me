package com.socialscreencontrol.presentation.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.core.model.RequestStatus
import com.socialscreencontrol.core.model.TimeRequest
import com.socialscreencontrol.domain.repository.RequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RequestTimeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: RequestRepository
) : ViewModel() {
    fun submit(groupId: String, packageName: String, extraMinutes: Int, reason: String) = viewModelScope.launch {
        val request = TimeRequest(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            requesterId = auth.currentUser?.uid.orEmpty(),
            packageName = packageName,
            extraMinutes = extraMinutes,
            reason = reason,
            status = RequestStatus.PENDING,
            createdAt = Timestamp.now()
        )
        repository.createRequest(request)
    }
}
