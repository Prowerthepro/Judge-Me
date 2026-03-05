package com.socialscreencontrol.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.R
import com.socialscreencontrol.core.model.AppLimit
import com.socialscreencontrol.core.model.UsageSnapshot
import com.socialscreencontrol.core.util.todayKey
import com.socialscreencontrol.domain.repository.UsageRepository
import com.socialscreencontrol.presentation.blocking.BlockingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class UsageTrackingService : Service() {

    @Inject lateinit var usageRepository: UsageRepository
    @Inject lateinit var auth: FirebaseAuth

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startForeground(42, foregroundNotification())
        serviceScope.launch { monitorLoop() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private suspend fun monitorLoop() {
        while (true) {
            val uid = auth.currentUser?.uid
            if (!uid.isNullOrBlank()) {
                val limits = usageRepository.observeLimits(uid).first()
                limits.filter { it.enabled }.forEach { limit ->
                    val usedMinutes = queryMinutesUsed(limit.packageName)
                    usageRepository.saveUsageSnapshot(
                        UsageSnapshot(
                            userId = uid,
                            packageName = limit.packageName,
                            dateKey = todayKey(),
                            usedMinutes = usedMinutes
                        )
                    )
                    if (usedMinutes >= limit.dailyLimitMinutes + limit.extraGrantedMinutes && isPackageForeground(limit.packageName)) {
                        showBlockingScreen(limit)
                    }
                }
            }
            delay(60_000)
        }
    }

    private fun queryMinutesUsed(packageName: String): Int {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        val stats = usm.queryAndAggregateUsageStats(start, end)
        val totalMs = stats[packageName]?.totalTimeInForeground ?: 0L
        return (totalMs / 60_000).toInt()
    }

    private fun isPackageForeground(packageName: String): Boolean {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = end - 60_000
        val events = usm.queryEvents(start, end)
        val event = android.app.usage.UsageEvents.Event()
        var latestPackage = ""
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                latestPackage = event.packageName
            }
        }
        return latestPackage == packageName
    }

    private fun showBlockingScreen(limit: AppLimit) {
        val intent = Intent(this, BlockingActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(BlockingActivity.EXTRA_PACKAGE, limit.appName)
        }
        startActivity(intent)
    }

    private fun foregroundNotification(): Notification {
        val channelId = "usage_monitor"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Usage Monitor", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Social Screen Control")
            .setContentText("Monitoring app usage and enforcing limits")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }
}
