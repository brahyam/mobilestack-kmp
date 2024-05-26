package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.RootComponent.Child
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.provider.DefaultNotificationProvider
import com.zenithapps.mobilestack.provider.FirebaseAuthProvider
import com.zenithapps.mobilestack.provider.FirebaseRemoteConfigProvider
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification
import com.zenithapps.mobilestack.provider.OSCapabilityProvider
import com.zenithapps.mobilestack.provider.REVENUE_CAT_ANDROID_API_KEY
import com.zenithapps.mobilestack.provider.REVENUE_CAT_IOS_API_KEY
import com.zenithapps.mobilestack.repository.FirebaseUserRepository
import com.zenithapps.mobilestack.useCase.PurchaseUseCase
import com.zenithapps.mobilestack.useCase.SignInUseCase
import com.zenithapps.mobilestack.useCase.SignOutUseCase
import com.zenithapps.mobilestack.useCase.SignUpUseCase
import com.zenithapps.mobilestack.util.createCoroutineScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigClientException
import dev.gitlive.firebase.remoteconfig.remoteConfig
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    val notificationComponent: NotificationComponent

    sealed interface Child {
        class Loading(val component: LoadingComponent) : Child
        class SignUp(val component: SignUpComponent) : Child
        class SignIn(val component: SignInComponent) : Child
        class ResetPassword(val component: ResetPasswordComponent) : Child
        class Profile(val component: ProfileComponent) : Child
        class Purchase(val component: PurchaseComponent) : Child
        class Welcome(val component: WelcomeComponent) : Child
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val billingProvider: BillingProvider,
    private val osCapabilityProvider: OSCapabilityProvider,
    private val analyticsProvider: AnalyticsProvider,
) : RootComponent, ComponentContext by componentContext {

    private val scope = createCoroutineScope()

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Loading,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private val notificationProvider by lazy {
        DefaultNotificationProvider()
    }

    override val notificationComponent = DefaultNotificationComponent(
        componentContext = componentContext,
        notificationProvider = notificationProvider
    )

    private val authProvider by lazy {
        FirebaseAuthProvider(
            firebaseAuth = Firebase.auth
        )
    }

    private val userRepository by lazy {
        FirebaseUserRepository(
            firebaseFirestore = Firebase.firestore
        )
    }

    private val remoteConfigProvider by lazy {
        FirebaseRemoteConfigProvider(
            remoteConfig = Firebase.remoteConfig
        )
    }

    private val signUpUseCase by lazy {
        SignUpUseCase(
            userRepository = userRepository,
            authProvider = authProvider,
            billingProvider = billingProvider,
        )
    }

    private val signInUseCase by lazy {
        SignInUseCase(
            authProvider = authProvider,
            billingProvider = billingProvider,
        )
    }

    private val signOutUseCase by lazy {
        SignOutUseCase(
            authProvider = authProvider,
            billingProvider = billingProvider,
        )
    }

    private val purchaseUseCase by lazy {
        PurchaseUseCase(
            authProvider = authProvider,
            billingProvider = billingProvider,
            userRepository = userRepository,
            signUp = signUpUseCase
        )
    }

    init {
        setup()
    }

    private fun setup() {
        scope.launch {
            try {
                remoteConfigProvider.fetchAndActivate()
                billingProvider.configure(getBillingApiKey(), authProvider.getAuthUser()?.id)
                if (authProvider.getAuthUser() != null) {
                    navigation.replaceAll(Config.Profile)
                } else {
                    navigation.replaceAll(Config.Welcome)
                }
            } catch (e: FirebaseRemoteConfigClientException) {
                Napier.e { "Failed to load configuration: ${e.message}" }
                notificationProvider.showNotification(
                    Notification(
                        message = "Failed to load configuration",
                        withDismissAction = false,
                        action = Notification.Action(title = "Retry", onClick = { setup() }),
                    )
                )
            }
        }
    }

    private fun getBillingApiKey(): String {
        val key = when (osCapabilityProvider.getPlatform()) {
            OSCapabilityProvider.Platform.ANDROID -> REVENUE_CAT_ANDROID_API_KEY
            OSCapabilityProvider.Platform.IOS -> REVENUE_CAT_IOS_API_KEY
        }
        return remoteConfigProvider.getString(key)
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child =
        when (config) {
            Config.Loading -> Child.Loading(
                component = DefaultLoadingComponent(componentContext)
            )

            Config.SignUp -> Child.SignUp(
                component = DefaultSignUpComponent(
                    componentContext = componentContext,
                    signUp = signUpUseCase,
                    analyticsProvider = analyticsProvider,
                    notificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            SignUpComponent.Output.ForgotPassword -> navigation.pushToFront(Config.ResetPassword)
                            SignUpComponent.Output.SignIn -> navigation.pushToFront(Config.SignIn)
                            SignUpComponent.Output.Back -> navigation.pop()
                            SignUpComponent.Output.Authenticated -> navigation.replaceAll(Config.Profile)
                        }
                    }
                )
            )

            Config.Profile -> Child.Profile(
                component = DefaultProfileComponent(
                    componentContext = componentContext,
                    userRepository = userRepository,
                    authProvider = authProvider,
                    billingProvider = billingProvider,
                    osCapabilityProvider = osCapabilityProvider,
                    analyticsProvider = analyticsProvider,
                    notificationProvider = notificationProvider,
                    signOut = signOutUseCase,
                    onOutput = { output ->
                        when (output) {
                            ProfileComponent.Output.Purchase -> navigation.pushToFront(Config.Purchase)
                            ProfileComponent.Output.GoBack -> navigation.pop()
                            ProfileComponent.Output.SignedOut -> navigation.replaceAll(Config.Welcome)
                        }
                    }
                )
            )

            Config.SignIn -> Child.SignIn(
                component = DefaultSignInComponent(
                    componentContext = componentContext,
                    signIn = signInUseCase,
                    analyticsProvider = analyticsProvider,
                    notificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            SignInComponent.Output.ResetPassword -> navigation.pushToFront(Config.ResetPassword)
                            SignInComponent.Output.SignUp -> navigation.pushToFront(Config.SignUp)
                            SignInComponent.Output.Back -> navigation.pop()
                            SignInComponent.Output.Authenticated -> navigation.replaceAll(Config.Profile)
                        }
                    }
                )
            )

            Config.ResetPassword -> Child.ResetPassword(
                component = DefaultResetPasswordComponent(
                    componentContext = componentContext,
                    authProvider = authProvider,
                    analyticsProvider = analyticsProvider,
                    notificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            ResetPasswordComponent.Output.Back -> navigation.pop()
                        }
                    }
                )
            )

            Config.Purchase -> Child.Purchase(
                component = DefaultPurchaseComponent(
                    componentContext = componentContext,
                    billingProvider = billingProvider,
                    authProvider = authProvider,
                    purchase = purchaseUseCase,
                    analyticsProvider = analyticsProvider,
                    notificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            PurchaseComponent.Output.Back -> navigation.pop()
                            PurchaseComponent.Output.Purchased -> navigation.replaceAll(Config.Profile)
                        }
                    }
                )
            )

            Config.Welcome -> Child.Welcome(
                component = DefaultWelcomeComponent(
                    componentContext = componentContext,
                    analyticsProvider = analyticsProvider,
                    onOutput = { output ->
                        when (output) {
                            WelcomeComponent.Output.SignUp -> navigation.pushToFront(Config.SignUp)
                            WelcomeComponent.Output.Purchase -> navigation.pushToFront(Config.Purchase)
                        }
                    }
                )
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Loading : Config

        @Serializable
        data object SignUp : Config

        @Serializable
        data object SignIn : Config

        @Serializable
        data object ResetPassword : Config

        @Serializable
        data object Profile : Config

        @Serializable
        data object Purchase : Config

        @Serializable
        data object Welcome : Config
    }
}