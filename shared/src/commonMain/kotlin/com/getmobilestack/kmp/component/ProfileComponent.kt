package com.getmobilestack.kmp.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.getmobilestack.kmp.component.ProfileComponent.Model
import com.getmobilestack.kmp.component.ProfileComponent.Output
import com.getmobilestack.kmp.model.CustomerBillingInfo
import com.getmobilestack.kmp.model.User
import com.getmobilestack.kmp.provider.AnalyticsProvider
import com.getmobilestack.kmp.provider.AuthProvider
import com.getmobilestack.kmp.provider.BillingProvider
import com.getmobilestack.kmp.provider.InAppNotificationProvider
import com.getmobilestack.kmp.provider.InAppNotificationProvider.Notification
import com.getmobilestack.kmp.provider.OSCapabilityProvider
import com.getmobilestack.kmp.repository.UserRepository
import com.getmobilestack.kmp.useCase.DeleteAccountUseCase
import com.getmobilestack.kmp.useCase.SignOutUseCase
import com.getmobilestack.kmp.util.createCoroutineScope
import kotlinx.coroutines.launch

interface ProfileComponent {
    val model: Value<Model>

    data class Model(
        val initialLoading: Boolean = true,
        val loading: Boolean = false,
        val user: User? = null,
        val customerBillingInfo: CustomerBillingInfo? = null,
        val appVersion: String = "",
        val newEmail: String = "",
        val editModeEnabled: Boolean = false,
        val isAnonymous: Boolean = false,
        val canGoBack: Boolean
    )

    fun onSignOutTap()

    fun onPurchaseTap()

    fun onMarketingConsentChanged(consent: Boolean)

    fun onManagePurchasesTap()

    fun onRestorePurchasesTap()

    fun onHelpTap()

    fun onOpenSourceLibrariesTap()

    fun onEmailChanged(email: String)

    fun onSaveEmailTap()

    fun onDeleteAccountTap()

    fun onEnableEditModeTap()

    fun onBackTap()

    fun onMobileStackTap()

    fun onOnboardingTap()

    sealed interface Output {
        data object Purchase : Output
        data object SignedOut : Output
        data object GoBack : Output
        data object Onboarding : Output
    }
}

private const val SCREEN_NAME = "profile"

class DefaultProfileComponent(
    componentContext: ComponentContext,
    canGoBack: Boolean,
    private val userRepository: UserRepository,
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider,
    private val osCapabilityProvider: OSCapabilityProvider,
    private val analyticsProvider: AnalyticsProvider,
    private val inAppNotificationProvider: InAppNotificationProvider,
    private val signOut: SignOutUseCase,
    private val deleteAccount: DeleteAccountUseCase,
    private val onOutput: (Output) -> Unit
) : ProfileComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model(canGoBack = canGoBack))

    private val scope = createCoroutineScope()

    init {
        lifecycle.doOnResume {
            model.value = model.value.copy(initialLoading = true)
            scope.launch {
                val user = userRepository.getUser() ?: userRepository.createUser()
                val customerInfo = billingProvider.getCustomerBillingInfo()
                model.value = model.value.copy(
                    initialLoading = false,
                    user = user,
                    customerBillingInfo = customerInfo,
                    appVersion = osCapabilityProvider.getAppVersion(),
                    newEmail = user.email ?: "",
                    isAnonymous = user.email == null
                )
            }
        }
    }

    override fun onSignOutTap() {
        analyticsProvider.logEvent(
            eventName = "sign_out_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                signOut()
                onOutput(Output.SignedOut)
            } catch (e: Exception) {
                inAppNotificationProvider.showNotification(
                    Notification(
                        message = e.message ?: "An error occurred"
                    )
                )
            } finally {
                model.value = model.value.copy(loading = false)
            }
        }
    }

    override fun onPurchaseTap() {
        analyticsProvider.logEvent(
            eventName = "purchase_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        if (model.value.customerBillingInfo?.entitlements?.isNotEmpty() == true) return
        onOutput(Output.Purchase)
    }

    override fun onMarketingConsentChanged(consent: Boolean) {
        analyticsProvider.logEvent(
            eventName = "marketing_consent_changed",
            screenName = SCREEN_NAME,
            params = mapOf("consent" to consent)
        )
        scope.launch {
            val user = model.value.user ?: return@launch
            model.value = model.value.copy(loading = true)
            userRepository.updateUser(user.copy(marketingConsent = consent))
            model.value =
                model.value.copy(user = user.copy(marketingConsent = consent), loading = false)
        }
    }

    override fun onManagePurchasesTap() {
        analyticsProvider.logEvent(
            eventName = "manage_purchases_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        val billingManagementUrl = model.value.customerBillingInfo?.managementUrl
        if (billingManagementUrl.isNullOrEmpty() || billingManagementUrl == "null") {
            osCapabilityProvider.managePurchases()
        } else {
            osCapabilityProvider.openUrl(billingManagementUrl)
        }
    }

    override fun onRestorePurchasesTap() {
        analyticsProvider.logEvent(
            eventName = "restore_purchases_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                billingProvider.restorePurchases()
                val customerInfo = billingProvider.getCustomerBillingInfo()
                model.value = model.value.copy(loading = false, customerBillingInfo = customerInfo)
                inAppNotificationProvider.showNotification(
                    Notification(message = "Purchases restored")
                )
            } catch (e: Exception) {
                model.value = model.value.copy(loading = false)
                inAppNotificationProvider.showNotification(
                    Notification(
                        message = e.message ?: "An error occurred"
                    )
                )
            }
        }
    }

    override fun onHelpTap() {
        analyticsProvider.logEvent(
            eventName = "help_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        osCapabilityProvider.openUrl("https://github.com/brahyam/mobilestack-kmp/issues")
    }

    override fun onOpenSourceLibrariesTap() {
        analyticsProvider.logEvent(
            eventName = "open_source_libraries_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        osCapabilityProvider.openUrl("https://github.com/brahyam/mobilestack-kmp#third-party-licenses")
    }

    override fun onEmailChanged(email: String) {
        model.value = model.value.copy(newEmail = email)
    }

    override fun onSaveEmailTap() {
        val newEmail = model.value.newEmail
        analyticsProvider.logEvent(
            eventName = "save_email_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        if (newEmail.isEmpty()) {
            inAppNotificationProvider.showNotification(Notification(message = "Email is required"))
            return
        }
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
        if (!emailRegex.matches(newEmail)) {
            inAppNotificationProvider.showNotification(Notification(message = "Invalid email"))
            return
        }
        model.value = model.value.copy(loading = true)
        scope.launch {
            val user = model.value.user ?: return@launch
            billingProvider.setEmail(newEmail)
            userRepository.updateUser(user.copy(email = newEmail))
            model.value = model.value.copy(
                user = user.copy(email = newEmail),
                loading = false,
                editModeEnabled = false
            )
        }
    }

    override fun onDeleteAccountTap() {
        analyticsProvider.logEvent(
            eventName = "delete_account_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                deleteAccount()
                onOutput(Output.SignedOut)
            } catch (e: Exception) {
                model.value = model.value.copy(loading = false)
                inAppNotificationProvider.showNotification(
                    Notification(
                        message = e.message ?: "An error occurred"
                    )
                )
            }
        }
    }

    override fun onEnableEditModeTap() {
        analyticsProvider.logEvent(
            eventName = "enable_edit_mode_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(editModeEnabled = true)
    }

    override fun onBackTap() {
        onOutput(Output.GoBack)
    }

    override fun onMobileStackTap() {
        osCapabilityProvider.openUrl("https://github.com/brahyam/mobilestack-kmp")
    }

    override fun onOnboardingTap() {
        onOutput(Output.Onboarding)
    }
}