package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.ResetPasswordComponent.Model
import com.zenithapps.mobilestack.component.ResetPasswordComponent.Output
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.NotificationProvider
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification
import com.zenithapps.mobilestack.util.createCoroutineScope
import kotlinx.coroutines.launch

interface ResetPasswordComponent {
    val model: Value<Model>

    data class Model(
        val loading: Boolean = false,
        val email: String = ""
    )

    fun onEmailChanged(email: String)
    fun onResetPasswordTap()
    fun onBackTap()

    sealed interface Output {
        data object Back : Output
    }
}

private const val SCREEN_NAME = "reset_password"

class DefaultResetPasswordComponent(
    componentContext: ComponentContext,
    private val authProvider: AuthProvider,
    private val analyticsProvider: AnalyticsProvider,
    private val notificationProvider: NotificationProvider,
    private val onOutput: (Output) -> Unit
) : ResetPasswordComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())
    private val scope = createCoroutineScope()

    override fun onEmailChanged(email: String) {
        model.value = model.value.copy(email = email)
    }

    override fun onResetPasswordTap() {
        analyticsProvider.logEvent(
            eventName = "reset_password_tap",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                authProvider.resetPassword(model.value.email)
                notificationProvider.showNotification(
                    Notification(message = "Password reset email sent")
                )
                onOutput(Output.Back)
            } catch (e: Exception) {
                model.value = model.value.copy(loading = false)
                val message = when {
                    e.message == null -> "An error occurred"
                    e.message!!.contains("badly formatted") -> "Invalid email"
                    e.message!!.contains("empty") -> "Email cannot be empty"
                    else -> e.message!!
                }
                notificationProvider.showNotification(
                    Notification(message = message)
                )
            }
        }
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
