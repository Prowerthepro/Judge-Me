package com.socialscreencontrol.presentation.limits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.core.model.AppLimit
import com.socialscreencontrol.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LimitsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val usageRepository: UsageRepository
) : ViewModel() {
    private val userId = auth.currentUser?.uid.orEmpty()
    val limits: StateFlow<List<AppLimit>> = usageRepository.observeLimits(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun saveLimit(limit: AppLimit) = viewModelScope.launch {
        usageRepository.upsertLimit(userId, limit)
    }
}
