package com.socialscreencontrol.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.core.model.AppLimit
import com.socialscreencontrol.core.model.UsageSnapshot
import com.socialscreencontrol.core.util.minutesAsReadable
import com.socialscreencontrol.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val loading: Boolean = true,
    val usageCards: List<UsageCard> = emptyList(),
    val totalMinutes: Int = 0,
    val streakDays: Int = 0
)

data class UsageCard(
    val appName: String,
    val packageName: String,
    val usedMinutes: Int,
    val totalAllowedMinutes: Int,
    val progress: Float,
    val statusLabel: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val usageRepository: UsageRepository
) : ViewModel() {
    private val userId: String = auth.currentUser?.uid.orEmpty()
    private val loadingState = MutableStateFlow(true)

    val uiState: StateFlow<HomeUiState> = combine(
        usageRepository.observeLimits(userId),
        usageRepository.observeUsageToday(userId),
        loadingState
    ) { limits, usage, loading ->
        val cards = buildCards(limits, usage)
        HomeUiState(
            loading = loading,
            usageCards = cards,
            totalMinutes = usage.sumOf { it.usedMinutes },
            streakDays = if (cards.all { it.usedMinutes <= it.totalAllowedMinutes }) 1 else 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch { loadingState.value = false }
    }

    private fun buildCards(limits: List<AppLimit>, usage: List<UsageSnapshot>): List<UsageCard> {
        val usageByPackage = usage.associateBy { it.packageName }
        return limits.map { limit ->
            val minutes = usageByPackage[limit.packageName]?.usedMinutes ?: 0
            val allowed = limit.dailyLimitMinutes + limit.extraGrantedMinutes
            UsageCard(
                appName = limit.appName,
                packageName = limit.packageName,
                usedMinutes = minutes,
                totalAllowedMinutes = allowed,
                progress = (minutes.toFloat() / allowed.coerceAtLeast(1)).coerceIn(0f, 1f),
                statusLabel = "${minutes.minutesAsReadable()} / ${allowed.minutesAsReadable()}"
            )
        }
    }
}
