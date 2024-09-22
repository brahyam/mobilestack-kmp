package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.zenithapps.mobilestack.component.ProfileComponent.Model
import com.zenithapps.mobilestack.component.ProfileComponent.Output
import com.zenithapps.mobilestack.model.CustomerBillingInfo
import com.zenithapps.mobilestack.model.User
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.provider.InAppNotificationProvider
import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification
import com.zenithapps.mobilestack.provider.OSCapabilityProvider
import com.zenithapps.mobilestack.repository.UserRepository
import com.zenithapps.mobilestack.useCase.DeleteAccountUseCase
import com.zenithapps.mobilestack.useCase.SignOutUseCase
import com.zenithapps.mobilestack.util.createCoroutineScope
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

    fun onPrivacyPolicyTap()

    fun onTermsOfServiceTap()

    fun onOpenSourceLibrariesTap()

    fun onEmailChanged(email: String)

    fun onSaveEmailTap()

    fun onDeleteAccountTap()

    fun onEnableEditModeTap()

    fun onBackTap()

    fun onMobileStackTap()

    sealed interface Output {
        data object Purchase : Output
        data object SignedOut : Output
        data object GoBack : Output
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
                val authUser = authProvider.getAuthUser() ?: return@launch
                val user =
                    userRepository.getUser(authUser.id) ?: userRepository.createUser(authUser.id)
                val customerInfo = billingProvider.getCustomerBillingInfo()
                model.value = model.value.copy(
                    initialLoading = false,
                    user = user,
                    customerBillingInfo = customerInfo,
                    appVersion = osCapabilityProvider.getAppVersion(),
                    newEmail = user.email ?: "",
                    isAnonymous = authUser.isAnonymous
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
        osCapabilityProvider.openUrl("mailto:support@getmobilestack.com")
    }

    override fun onPrivacyPolicyTap() {
        analyticsProvider.logEvent(
            eventName = "privacy_policy_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        osCapabilityProvider.openUrl("https://getmobilestack.com/privacy-policy/")
    }

    override fun onTermsOfServiceTap() {
        analyticsProvider.logEvent(
            eventName = "terms_of_service_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        osCapabilityProvider.openUrl("https://getmobilestack.com/terms-of-use/")
    }

    override fun onOpenSourceLibrariesTap() {
        analyticsProvider.logEvent(
            eventName = "open_source_libraries_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        osCapabilityProvider.openUrl("https://getmobilestack.com/open-source-libraries/")
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
        osCapabilityProvider.openUrl("https://getmobilestack.com/")
    }
}