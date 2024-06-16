package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.RemotePaywallComponent.Model
import com.zenithapps.mobilestack.component.RemotePaywallComponent.Output

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
    private val onOutput: (Output) -> Unit
) : RemotePaywallComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())

    override fun onDismissTap() {
        onOutput(Output.Dismissed)
    }

    override fun onPurchaseStarted() {

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