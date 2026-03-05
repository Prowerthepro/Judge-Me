package com.socialscreencontrol.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.socialscreencontrol.core.model.UserProfile
import com.socialscreencontrol.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun observeAuthUser(): Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { authState ->
            val firebaseUser = authState.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection("users").document(firebaseUser.uid).get()
                    .addOnSuccessListener { doc ->
                        val profile = doc.toObject(UserProfile::class.java)
                            ?: UserProfile(
                                id = firebaseUser.uid,
                                phoneNumber = firebaseUser.phoneNumber.orEmpty(),
                                displayName = firebaseUser.phoneNumber.orEmpty()
                            )
                        trySend(profile)
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun requestPhoneAuth(phone: String) {
        // OTP dispatch should be initiated from UI layer with activity context and callbacks.
        check(phone.isNotBlank()) { "Phone number must not be blank" }
    }

    override suspend fun verifyOtp(verificationId: String, code: String) {
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential).await()
    }

    override suspend fun createOrUpdateProfile(name: String) {
        val uid = auth.currentUser?.uid ?: return
        val phone = auth.currentUser?.phoneNumber.orEmpty()
        val profile = UserProfile(id = uid, phoneNumber = phone, displayName = name)
        firestore.collection("users").document(uid).set(profile).await()
    }
}
