package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.SignUpComponent.Model
import com.zenithapps.mobilestack.component.SignUpComponent.Output
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.NotificationProvider
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification
import com.zenithapps.mobilestack.useCase.SignUpUseCase
import com.zenithapps.mobilestack.useCase.SignUpUseCase.SignUpWithEmailException.EmailAlreadyExists
import com.zenithapps.mobilestack.useCase.SignUpUseCase.SignUpWithEmailException.EmptyEmailOrPassword
import com.zenithapps.mobilestack.useCase.SignUpUseCase.SignUpWithEmailException.InvalidEmail
import com.zenithapps.mobilestack.useCase.SignUpUseCase.SignUpWithEmailException.InvalidPassword
import com.zenithapps.mobilestack.useCase.SignUpUseCase.SignUpWithEmailException.Other
import com.zenithapps.mobilestack.util.createCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

interface SignUpComponent {
    val model: Value<Model>

    data class Model(
        val email: String = "",
        val password: String = "",
        val birthdate: LocalDateTime? = null,
        val marketingConsent: Boolean = false,
        val loading: Boolean = false
    )

    fun onSignUpTap()

    fun onEmailChanged(email: String)

    fun onPasswordChanged(password: String)

    fun onMarketingConsentChanged(consent: Boolean)

    fun onBirthdateChanged(date: LocalDateTime)

    fun onSignInTap()

    fun onBackTap()

    fun onSignUpAnonymouslyTap()

    sealed interface Output {
        data object ForgotPassword : Output
        data object SignIn : Output
        data object Authenticated : Output
        data object Back : Output
    }
}

private const val SCREEN_NAME = "sign_up"

class DefaultSignUpComponent(
    componentContext: ComponentContext,
    private val signUp: SignUpUseCase,
    private val analyticsProvider: AnalyticsProvider,
    private val notificationProvider: NotificationProvider,
    private val onOutput: (Output) -> Unit
) : SignUpComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())

    private val scope = createCoroutineScope()

    override fun onSignUpTap() {
        analyticsProvider.logEvent(
            eventName = "sign_up_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                signUp(
                    email = model.value.email,
                    password = model.value.password,
                    marketingConsent = model.value.marketingConsent
                )
                onOutput(Output.Authenticated)
            } catch (exception: Exception) {
                val errorMessage = when (exception) {
                    EmailAlreadyExists -> "Email already exists"
                    InvalidEmail -> "Invalid email"
                    InvalidPassword -> "Invalid password"
                    EmptyEmailOrPassword -> "Email and password must not be empty"
                    is Other -> exception.reason
                    else -> {
                        exception.message ?: "Unknown error"
                    }
                }
                notificationProvider.showNotification(Notification(errorMessage))
            } finally {
                model.value = model.value.copy(loading = false)
            }
        }
    }

    override fun onEmailChanged(email: String) {
        model.value = model.value.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        model.value = model.value.copy(password = password)
    }

    override fun onMarketingConsentChanged(consent: Boolean) {
        model.value = model.value.copy(marketingConsent = consent)
    }

    override fun onBirthdateChanged(date: LocalDateTime) {
        model.value = model.value.copy(birthdate = date)
    }

    override fun onSignInTap() {
        analyticsProvider.logEvent(
            eventName = "sign_in_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        onOutput(Output.SignIn)
    }

    override fun onBackTap() {
        analyticsProvider.logEvent(
            eventName = "back_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        onOutput(Output.Back)
    }

    override fun onSignUpAnonymouslyTap() {
        analyticsProvider.logEvent(
            eventName = "sign_up_anonymously_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                signUp.anonymously()
                onOutput(Output.Authenticated)
            } catch (exception: Exception) {
                notificationProvider.showNotification(
                    Notification(exception.message ?: "Unknown error")
                )
            } finally {
                model.value = model.value.copy(loading = false)
            }
        }
    }
}