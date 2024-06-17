package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.RemotePaywallComponent.Model
import com.zenithapps.mobilestack.component.RemotePaywallComponent.Output
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.useCase.SignUpUseCase
import com.zenithapps.mobilestack.util.createCoroutineScope
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
    var purchaseCompleted: Boolean = false

    override fun onDismissTap() {
        if (!purchaseCompleted) { // RevenueCat lib fires dismiss right after purchase completed
            purchaseCompleted = false
            onOutput(Output.Dismissed)
        }
    }

    override fun onPurchaseStarted() {
        scope.launch {
            try {
                // Needs to be signed in to assign the purchase to the correct user
                if (!authProvider.isLoggedIn()) {
                    signUp.anonymously()
                }
            } catch (exception: Exception) {
                Napier.e(exception) { "Sign up failed" }
            }
        }
    }

    override fun onPurchaseCompleted() {
        purchaseCompleted = true
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