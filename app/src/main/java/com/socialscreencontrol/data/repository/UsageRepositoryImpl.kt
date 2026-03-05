package com.socialscreencontrol.data.repository

import com.socialscreencontrol.core.model.AppLimit
import com.socialscreencontrol.core.model.UsageSnapshot
import com.socialscreencontrol.core.util.todayKey
import com.socialscreencontrol.data.remote.FirebaseFirestoreService
import com.socialscreencontrol.domain.repository.UsageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val firestoreService: FirebaseFirestoreService
) : UsageRepository {
    override fun observeLimits(userId: String): Flow<List<AppLimit>> = firestoreService.observeLimits(userId)

    override suspend fun upsertLimit(userId: String, limit: AppLimit) {
        firestoreService.upsertLimit(userId, limit)
    }

    override suspend fun saveUsageSnapshot(snapshot: UsageSnapshot) {
        firestoreService.saveUsage(snapshot)
    }

    override fun observeUsageToday(userId: String): Flow<List<UsageSnapshot>> =
        firestoreService.observeUsageToday(userId, todayKey())

    override suspend fun grantExtraTime(userId: String, packageName: String, minutes: Int) {
        val existing = AppLimit(packageName = packageName, appName = packageName, extraGrantedMinutes = minutes)
        firestoreService.upsertLimit(userId, existing)
    }
}
