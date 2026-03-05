package com.socialscreencontrol.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class FcmTokenSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val uid = auth.currentUser?.uid ?: return Result.success()
        val token = FirebaseMessaging.getInstance().token.await()
        firestore.collection("users").document(uid).update("fcmToken", token).await()
        return Result.success()
    }
}
