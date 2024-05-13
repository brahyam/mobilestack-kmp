package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.SignInComponent.Model
import com.zenithapps.mobilestack.component.SignInComponent.Output
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.NotificationProvider
import com.zenithapps.mobilestack.useCase.SignInUseCase
import com.zenithapps.mobilestack.useCase.SignInUseCase.SignInError
import com.zenithapps.mobilestack.util.Result
import com.zenithapps.mobilestack.util.createCoroutineScope
import kotlinx.coroutines.launch

interface SignInComponent {
    val model: Value<Model>

    data class Model(
        val loading: Boolean = false,
        val email: String = "",
        val password: String = ""
    )

    fun onSignInTap()
    fun onSignUpTap()
    fun onEmailChanged(email: String)
    fun onPasswordChanged(password: String)
    fun onResetPasswordTap()

    fun onBackTap()

    sealed interface Output {
        data object ResetPassword : Output
        data object SignUp : Output
        data object Authenticated : Output
        data object Back : Output
    }
}

private const val SCREEN_NAME = "sign_in"

class DefaultSignInComponent(
    componentContext: ComponentContext,
    private val signIn: SignInUseCase,
    private val analyticsProvider: AnalyticsProvider,
    private val notificationProvider: NotificationProvider,
    private val onOutput: (Output) -> Unit
) : SignInComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())

    private val scope = createCoroutineScope()

    override fun onSignInTap() {
        analyticsProvider.logEvent(
            eventName = "sign_in_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        if (model.value.email.isEmpty() || model.value.password.isEmpty()) {
            notificationProvider.showNotification(
                NotificationProvider.Notification(
                    message = "Email and password are required"
                )
            )
            return
        }
        scope.launch {
            model.value = model.value.copy(loading = true)
            when (val result = signIn(email = model.value.email, password = model.value.password)) {
                is Result.Success -> {
                    model.value = model.value.copy(loading = false)
                    onOutput(Output.Authenticated)
                }
                is Result.Error -> {
                    model.value = model.value.copy(loading = false)
                    notificationProvider.showNotification(
                        NotificationProvider.Notification(
                            message = when (result.error) {
                                is SignInError.InvalidCredentials -> "Invalid credentials"
                                is SignInError.Other -> result.error.reason
                                is SignInError.InvalidEmail -> "Invalid email"
                            }
                        )
                    )
                }
            }
        }
    }

    override fun onSignUpTap() {
        analyticsProvider.logEvent(
            eventName = "sign_up_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        onOutput(Output.SignUp)
    }

    override fun onEmailChanged(email: String) {
        model.value = model.value.copy(email = email)
    }

    override fun onPasswordChanged(password: String) {
        model.value = model.value.copy(password = password)
    }

    override fun onResetPasswordTap() {
        analyticsProvider.logEvent(
            eventName = "reset_password_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        onOutput(Output.ResetPassword)
    }

    override fun onBackTap() {
        analyticsProvider.logEvent(
            eventName = "back_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        onOutput(Output.Back)
    }
}