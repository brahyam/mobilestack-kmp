package com.zenithapps.mobilestack.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.mmk.kmprevenuecat.purchases.data.CustomerInfo
import com.mmk.kmprevenuecat.purchases.ui.Paywall
import com.mmk.kmprevenuecat.purchases.ui.PaywallListener
import com.zenithapps.mobilestack.component.RemotePaywallComponent

@Composable
fun RemotePaywallScreen(component: RemotePaywallComponent) {
    val model by component.model.subscribeAsState()
    Paywall(
        shouldDisplayDismissButton = model.dismissible,
        onDismiss = component::onDismissTap,
        listener = object : PaywallListener {
            override fun onPurchaseStarted() {
                component.onPurchaseStarted()
            }

            override fun onPurchaseCompleted(customerInfo: CustomerInfo?) {
                component.onPurchaseCompleted()
            }

            override fun onPurchaseError(error: String?) {
                component.onPurchaseError(error)
            }

            override fun onPurchaseCancelled() {
                component.onPurchaseCancelled()
            }

            override fun onRestoreStarted() {
                component.onRestoreStarted()
            }

            override fun onRestoreCompleted(customerInfo: CustomerInfo?) {
                component.onRestoreCompleted()
            }

            override fun onRestoreError(error: String?) {
                component.onRestoreError(error)
            }
        }
    )
}