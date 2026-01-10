package com.getmobilestack.kmp.provider

import com.getmobilestack.kmp.repository.FirebaseUserRepository
import com.getmobilestack.kmp.repository.UserRepository
import com.getmobilestack.kmp.useCase.DeleteAccountUseCase
import com.getmobilestack.kmp.useCase.SignInUseCase
import com.getmobilestack.kmp.useCase.SignOutUseCase
import com.getmobilestack.kmp.useCase.SignUpUseCase
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
    val aiProvider: AiProvider
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

    override val aiProvider by lazy {
        val apiKey = remoteConfigProvider.getString("OPENAI_API_KEY")
        if (apiKey.isNotEmpty()) {
            OpenAiProvider(apiKey = apiKey)
        } else {
            MockAiProvider()
        }
    }
}