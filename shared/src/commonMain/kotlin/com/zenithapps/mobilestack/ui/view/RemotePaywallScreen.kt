package com.zenithapps.mobilestack.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.mmk.kmprevenuecat.purchases.data.CustomerInfo
import com.mmk.kmprevenuecat.purchases.ui.Paywall
import com.mmk.kmprevenuecat.purchases.ui.PaywallListener
import com.zenithapps.mobilestack.component.RemotePaywallComponent
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import com.zenithapps.mobilestack.ui.widget.MSTopAppBar

@Composable
fun RemotePaywallScreen(component: RemotePaywallComponent) {
    val model by component.model.subscribeAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                MSTopAppBar(
                    title = "",
                    onBackTap = component::onDismissTap
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Thank you, you can go back now.",
                    style = MaterialTheme.typography.displaySmall,
                )
                MSFilledButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    text = "Go back",
                    onClick = component::onDismissTap
                )
            }
        }
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
}