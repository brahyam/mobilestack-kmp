package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.zenithapps.mobilestack.component.ProfileComponent.Model
import com.zenithapps.mobilestack.component.ProfileComponent.Output
import com.zenithapps.mobilestack.model.User
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.provider.NotificationProvider
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification
import com.zenithapps.mobilestack.provider.OSCapabilityProvider
import com.zenithapps.mobilestack.repository.UserRepository
import com.zenithapps.mobilestack.useCase.SignOutUseCase
import com.zenithapps.mobilestack.util.Result
import com.zenithapps.mobilestack.util.createCoroutineScope
import kotlinx.coroutines.launch

interface ProfileComponent {
    val model: Value<Model>

    data class Model(
        val loading: Boolean = false,
        val user: User? = null,
        val customerInfo: BillingProvider.CustomerInfo? = null,
        val appVersion: String = "",
        val newEmail: String = "",
        val editModeEnabled: Boolean = false
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

    sealed interface Output {
        data object Purchase : Output
        data object SignedOut : Output
        data object GoBack : Output
    }
}

private const val SCREEN_NAME = "profile"

class DefaultProfileComponent(
    componentContext: ComponentContext,
    private val userRepository: UserRepository,
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider,
    private val osCapabilityProvider: OSCapabilityProvider,
    private val analyticsProvider: AnalyticsProvider,
    private val notificationProvider: NotificationProvider,
    private val signOut: SignOutUseCase,
    private val onOutput: (Output) -> Unit
) : ProfileComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())

    private val scope = createCoroutineScope()

    init {
        lifecycle.doOnResume {
            model.value = model.value.copy(loading = true)
            scope.launch {
                val authUser = authProvider.getAuthUser() ?: return@launch
                val user =
                    userRepository.getUser(authUser.id) ?: userRepository.createUser(authUser.id)
                val customerInfo = billingProvider.getCustomerInfo()
                model.value = model.value.copy(
                    loading = false,
                    user = user,
                    customerInfo = customerInfo,
                    appVersion = osCapabilityProvider.getAppVersion(),
                    newEmail = user.email ?: "",
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
        scope.launch {
            model.value = model.value.copy(loading = true)
            when (val result = signOut()) {
                is Result.Success -> onOutput(Output.SignedOut)
                is Result.Error -> {
                    model.value = model.value.copy(loading = false)
                    notificationProvider.showNotification(Notification(result.error.reason))
                }
            }
        }
    }

    override fun onPurchaseTap() {
        analyticsProvider.logEvent(
            eventName = "purchase_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
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
        if (model.value.customerInfo?.purchases?.isEmpty() == true) {
            notificationProvider.showNotification(
                Notification(message = "You don't have any purchases yet")
            )
            return
        }
        val billingManagementUrl = model.value.customerInfo?.managementUrl
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
                val customerInfo = billingProvider.getCustomerInfo()
                model.value = model.value.copy(loading = false, customerInfo = customerInfo)
                notificationProvider.showNotification(
                    Notification(message = "Purchases restored")
                )
            } catch (e: Exception) {
                model.value = model.value.copy(loading = false)
                notificationProvider.showNotification(
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
        osCapabilityProvider.openUrl("mailto:brahyam@getmobilestack.com")
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
            notificationProvider.showNotification(Notification(message = "Email is required"))
            return
        }
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
        if (!emailRegex.matches(newEmail)) {
            notificationProvider.showNotification(Notification(message = "Invalid email"))
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
                userRepository.deleteUser(model.value.user!!.id)
                authProvider.deleteAccount()
                onOutput(Output.SignedOut)
            } catch (e: Exception) {
                model.value = model.value.copy(loading = false)
                notificationProvider.showNotification(
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
}