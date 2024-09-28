package com.zenithapps.mobilestack.provider

import com.zenithapps.mobilestack.repository.FirebaseUserRepository
import com.zenithapps.mobilestack.repository.UserRepository
import com.zenithapps.mobilestack.useCase.DeleteAccountUseCase
import com.zenithapps.mobilestack.useCase.SignInUseCase
import com.zenithapps.mobilestack.useCase.SignOutUseCase
import com.zenithapps.mobilestack.useCase.SignUpUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.remoteconfig.remoteConfig

interface DependencyProvider {
    val billingProvider: BillingProvider
    val notificationProvider: InAppNotificationProvider
    val authProvider: AuthProvider
    val userRepository: UserRepository
    val remoteConfigProvider: RemoteConfigProvider
    val signUpUseCase: SignUpUseCase
    val signInUseCase: SignInUseCase
    val signOutUseCase: SignOutUseCase
    val deleteAccountUseCase: DeleteAccountUseCase
    val keyValueStorageProvider: KeyValueStorageProvider
}

class DefaultDependencyProvider : DependencyProvider {
    override val billingProvider by lazy {
        KMPRevenueCatBillingProvider()
    }

    override val notificationProvider by lazy {
        DefaultInAppNotificationProvider()
    }

    override val authProvider by lazy {
        FirebaseAuthProvider(
            firebaseAuth = Firebase.auth
        )
    }

    override val userRepository by lazy {
        FirebaseUserRepository(
            authProvider = authProvider,
            firebaseFirestore = Firebase.firestore
        )
    }

    override val remoteConfigProvider by lazy {
        FirebaseRemoteConfigProvider(
            remoteConfig = Firebase.remoteConfig
        )
    }

    override val signUpUseCase by lazy {
        SignUpUseCase(
            userRepository = userRepository,
            authProvider = authProvider,
            billingProvider = billingProvider,
        )
    }

    override val signInUseCase by lazy {
        SignInUseCase(
            authProvider = authProvider,
            billingProvider = billingProvider,
        )
    }

    override val signOutUseCase by lazy {
        SignOutUseCase(
            authProvider = authProvider,
            billingProvider = billingProvider,
        )
    }

    override val deleteAccountUseCase by lazy {
        DeleteAccountUseCase(
            userRepository = userRepository,
            authProvider = authProvider,
            billingProvider = billingProvider
        )
    }

    override val keyValueStorageProvider by lazy {
        KMPSettingsProvider()
    }
}