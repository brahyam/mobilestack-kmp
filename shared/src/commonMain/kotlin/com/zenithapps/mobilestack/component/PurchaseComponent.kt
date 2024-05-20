package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.zenithapps.mobilestack.component.PurchaseComponent.Model
import com.zenithapps.mobilestack.component.PurchaseComponent.Output
import com.zenithapps.mobilestack.model.Product
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.provider.NotificationProvider
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification.Duration
import com.zenithapps.mobilestack.useCase.PurchaseUseCase
import com.zenithapps.mobilestack.useCase.PurchaseUseCase.PurchaseException.Pending
import com.zenithapps.mobilestack.util.createCoroutineScope
import kotlinx.coroutines.launch

interface PurchaseComponent {
    val model: Value<Model>

    data class Model(
        val loading: Boolean = true,
        val products: List<Product> = emptyList()
    )

    fun onProductTap(product: Product)

    fun onBackTap()

    sealed interface Output {
        data object Back : Output
        data object Purchased : Output
    }
}

private const val SCREEN_NAME = "purchase"

class DefaultPurchaseComponent(
    componentContext: ComponentContext,
    private val billingProvider: BillingProvider,
    private val authProvider: AuthProvider,
    private val purchase: PurchaseUseCase,
    private val analyticsProvider: AnalyticsProvider,
    private val notificationProvider: NotificationProvider,
    private val onOutput: (Output) -> Unit
) : PurchaseComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())
    private val scope = createCoroutineScope()

    init {
        lifecycle.doOnResume {
            scope.launch {
                model.value = model.value.copy(loading = true)
                try {
                    val customerInfo = billingProvider.getCustomerBillingInfo()
                    val products =
                        billingProvider.getProducts().filter { it.id !in customerInfo.purchases }
                    model.value = model.value.copy(loading = false, products = products)
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
    }

    override fun onProductTap(product: Product) {
        analyticsProvider.logEvent(
            eventName = "product_tap",
            screenName = SCREEN_NAME,
            params = mapOf("product_id" to product.id, "product_name" to product.title)
        )
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                purchase(product.packageId)
                analyticsProvider.logEvent(
                    eventName = "product_purchased",
                    screenName = SCREEN_NAME,
                    params = mapOf(
                        "product_id" to product.id,
                        "product_name" to product.title
                    )
                )
                val purchaseSuccessMessage =
                    if (authProvider.getAuthUser()?.email != null) {
                        "Thank you for purchasing. You will receive an invite to MobileStack repo shortly via email."
                    } else {
                        "Thank you for purchasing. Please add your email to receive and invite to Github and Discord."
                    }
                notificationProvider.showNotification(
                    Notification(purchaseSuccessMessage, duration = Duration.LONG)
                )
                onOutput(Output.Purchased)
            } catch (exception: Exception) {
                if (exception is Pending) {
                    val pendingMessage =
                        if (authProvider.getAuthUser()?.email != null) {
                            "Thank you for purchasing. Your order is pending and you will receive MobileStack once your payment is processed."
                        } else {
                            "Thank you for purchasing. Your order is pending and you will receive MobileStack once your payment is processed. Please add your email to receive and invite to Github and Discord."
                        }
                    notificationProvider.showNotification(
                        Notification(message = pendingMessage, duration = Duration.LONG)
                    )
                    onOutput(Output.Purchased)
                } else {
                    notificationProvider.showNotification(
                        Notification(
                            message = exception.message ?: "An error occurred"
                        )
                    )
                }
            } finally {
                model.value = model.value.copy(loading = false)
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