package com.getmobilestack.kmp.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.getmobilestack.kmp.component.RemotePaywallComponent.Model
import com.getmobilestack.kmp.component.RemotePaywallComponent.Output
import com.getmobilestack.kmp.provider.AuthProvider
import com.getmobilestack.kmp.useCase.SignUpUseCase
import com.getmobilestack.kmp.util.createCoroutineScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

interface RemotePaywallComponent {
    val model: Value<Model>

    data class Model(
        val dismissible: Boolean = true
    )

    fun onDismissTap()
    fun onPurchaseStarted()
    fun onPurchaseCompleted()
    fun onPurchaseError(error: String?)
    fun onPurchaseCancelled()
    fun onRestoreStarted()
    fun onRestoreCompleted()
    fun onRestoreError(error: String?)
    sealed interface Output {
        data object Dismissed : Output
        data object PurchaseCompleted : Output
        data object PurchaseError : Output
        data object PurchaseCancelled : Output
        data object RestoreCompleted : Output
        data object RestoreError : Output
    }
}

class DefaultRemotePaywallComponent(
    componentContext: ComponentContext,
    private val authProvider: AuthProvider,
    private val signUp: SignUpUseCase,
    private val onOutput: (Output) -> Unit
) : RemotePaywallComponent, ComponentContext by componentContext {
    private val scope = createCoroutineScope()
    override val model = MutableValue(Model())

    override fun onDismissTap() {
        onOutput(Output.Dismissed)
    }

    override fun onPurchaseStarted() {
        scope.launch {
            try {
                if (!authProvider.isLoggedIn()) {
                    signUp.anonymously()
                }
            } catch (exception: Exception) {
                Napier.e(exception) { "Sign up failed" }
            }
        }
    }

    override fun onPurchaseCompleted() {
        onOutput(Output.PurchaseCompleted)
    }

    override fun onPurchaseError(error: String?) {
        onOutput(Output.PurchaseError)
    }

    override fun onPurchaseCancelled() {
        onOutput(Output.PurchaseCancelled)
    }

    override fun onRestoreStarted() {

    }

    override fun onRestoreCompleted() {
        onOutput(Output.RestoreCompleted)
    }

    override fun onRestoreError(error: String?) {
        onOutput(Output.RestoreError)
    }

}