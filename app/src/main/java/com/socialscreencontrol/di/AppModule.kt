package com.socialscreencontrol.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.socialscreencontrol.data.remote.FirebaseFirestoreService
import com.socialscreencontrol.data.repository.AuthRepositoryImpl
import com.socialscreencontrol.data.repository.GroupRepositoryImpl
import com.socialscreencontrol.data.repository.RequestRepositoryImpl
import com.socialscreencontrol.data.repository.UsageRepositoryImpl
import com.socialscreencontrol.domain.repository.AuthRepository
import com.socialscreencontrol.domain.repository.GroupRepository
import com.socialscreencontrol.domain.repository.RequestRepository
import com.socialscreencontrol.domain.repository.UsageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreService(db: FirebaseFirestore): FirebaseFirestoreService = FirebaseFirestoreService(db)

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideGroupRepository(impl: GroupRepositoryImpl): GroupRepository = impl

    @Provides
    @Singleton
    fun provideUsageRepository(impl: UsageRepositoryImpl): UsageRepository = impl

    @Provides
    @Singleton
    fun provideRequestRepository(impl: RequestRepositoryImpl): RequestRepository = impl

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
