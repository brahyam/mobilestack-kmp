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
import com.zenithapps.mobilestack.provider.DefaultDependencyProvider
import com.zenithapps.mobilestack.provider.DependencyProvider
import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification
import com.zenithapps.mobilestack.provider.OSCapabilityProvider
import com.zenithapps.mobilestack.provider.REVENUE_CAT_ANDROID_API_KEY
import com.zenithapps.mobilestack.provider.REVENUE_CAT_IOS_API_KEY
import com.zenithapps.mobilestack.util.createCoroutineScope
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfigClientException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    val inAppNotificationComponent: InAppNotificationComponent

    sealed interface Child {
        class Loading(val component: LoadingComponent) : Child
        class SignUp(val component: SignUpComponent) : Child
        class SignIn(val component: SignInComponent) : Child
        class ResetPassword(val component: ResetPasswordComponent) : Child
        class Profile(val component: ProfileComponent) : Child
        class Welcome(val component: WelcomeComponent) : Child
        class Home(val component: SampleAiHomeComponent) : Child
        class RemotePaywall(val component: RemotePaywallComponent) : Child
        class Onboarding(val component: OnboardingComponent) : Child
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val osCapabilityProvider: OSCapabilityProvider,
    private val analyticsProvider: AnalyticsProvider,
) : RootComponent, ComponentContext by componentContext,
    DependencyProvider by DefaultDependencyProvider() {

    private val scope = createCoroutineScope()

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Loading,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override val inAppNotificationComponent = DefaultInAppNotificationComponent(
        componentContext = componentContext,
        inAppNotificationProvider = notificationProvider
    )

    init {
        setup()
    }

    // TIP: runs when app opens
    private fun setup() {
        scope.launch {
            try {
                remoteConfigProvider.fetchAndActivate()
                billingProvider.configure(getBillingApiKey(), authProvider.getAuthUser()?.id)
                if (authProvider.isLoggedIn()) {
                    // TIP: Define here what screen to show if the user is authenticated
                    navigation.replaceAll(Config.Home)
                } else {
                    if (keyValueStorageProvider.getBoolean("onboarding_completed") == true) {
                        navigation.replaceAll(Config.Welcome)
                    } else {
                        navigation.replaceAll(Config.Onboarding)
                    }
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
                    inAppNotificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            SignUpComponent.Output.ForgotPassword -> navigation.pushToFront(Config.ResetPassword)
                            SignUpComponent.Output.SignIn -> navigation.pushToFront(Config.SignIn)
                            SignUpComponent.Output.Back -> navigation.pop()
                            SignUpComponent.Output.Authenticated -> navigation.replaceAll(Config.Home)
                        }
                    }
                )
            )

            is Config.Profile -> Child.Profile(
                component = DefaultProfileComponent(
                    componentContext = componentContext,
                    canGoBack = config.canGoBack,
                    userRepository = userRepository,
                    authProvider = authProvider,
                    billingProvider = billingProvider,
                    osCapabilityProvider = osCapabilityProvider,
                    analyticsProvider = analyticsProvider,
                    inAppNotificationProvider = notificationProvider,
                    signOut = signOutUseCase,
                    deleteAccount = deleteAccountUseCase,
                    onOutput = { output ->
                        when (output) {
                            ProfileComponent.Output.Purchase -> navigation.pushToFront(Config.RemotePaywall)
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
                    inAppNotificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            SignInComponent.Output.ResetPassword -> navigation.pushToFront(Config.ResetPassword)
                            SignInComponent.Output.SignUp -> navigation.pushToFront(Config.SignUp)
                            SignInComponent.Output.Back -> navigation.pop()
                            SignInComponent.Output.Authenticated -> navigation.replaceAll(
                                Config.Profile(
                                    canGoBack = false
                                )
                            )
                        }
                    }
                )
            )

            Config.ResetPassword -> Child.ResetPassword(
                component = DefaultResetPasswordComponent(
                    componentContext = componentContext,
                    authProvider = authProvider,
                    analyticsProvider = analyticsProvider,
                    inAppNotificationProvider = notificationProvider,
                    onOutput = { output ->
                        when (output) {
                            ResetPasswordComponent.Output.Back -> navigation.pop()
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
                            WelcomeComponent.Output.Purchase -> navigation.pushToFront(Config.RemotePaywall)
                        }
                    }
                )
            )

            Config.Home -> Child.Home(
                component = DefaultSampleAiHomeComponent(
                    componentContext = componentContext,
                    authProvider = authProvider,
                    signUp = signUpUseCase,
                    inAppNotificationProvider = notificationProvider,
                    aiProvider = aiProvider,
                    osCapabilityProvider = osCapabilityProvider,
                    onOutput = { output ->
                        when (output) {
                            SampleAiHomeComponent.Output.GoToProfile -> navigation.pushToFront(
                                Config.Profile(canGoBack = true)
                            )
                        }
                    }
                )
            )

            Config.RemotePaywall -> Child.RemotePaywall(
                component = DefaultRemotePaywallComponent(
                    componentContext = componentContext,
                    authProvider = authProvider,
                    signUp = signUpUseCase,
                    onOutput = { output ->
                        when (output) {
                            RemotePaywallComponent.Output.Dismissed -> navigation.pop()

                            RemotePaywallComponent.Output.PurchaseCancelled -> {}
                            RemotePaywallComponent.Output.PurchaseCompleted -> navigation.pushToFront(
                                Config.Profile(false)
                            )

                            RemotePaywallComponent.Output.PurchaseError -> {}
                            RemotePaywallComponent.Output.RestoreCompleted -> navigation.pushToFront(
                                Config.Profile(false)
                            )

                            RemotePaywallComponent.Output.RestoreError -> {}
                        }
                    }
                )
            )

            Config.Onboarding -> Child.Onboarding(
                component = DefaultOnboardingComponent(
                    componentContext = componentContext,
                    keyValueStorageProvider = keyValueStorageProvider,
                    analyticsProvider = analyticsProvider,
                    billingProvider = billingProvider,
                    onOutput = { output ->
                        when (output) {
                            OnboardingComponent.Output.Finished -> navigation.replaceAll(Config.Welcome)
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
        data class Profile(val canGoBack: Boolean = false) : Config

        @Serializable
        data object Welcome : Config

        @Serializable
        data object Home : Config

        @Serializable
        data object RemotePaywall : Config

        @Serializable
        data object Onboarding : Config
    }
}