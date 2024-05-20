package com.zenithapps.mobilestack.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.zenithapps.mobilestack.component.PurchaseComponent
import com.zenithapps.mobilestack.model.Product
import com.zenithapps.mobilestack.ui.widget.FeatureItem
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import com.zenithapps.mobilestack.ui.widget.MSTopAppBar
import com.zenithapps.mobilestack.ui.widget.NotIncludedItem

@Composable
fun PurchaseScreen(component: PurchaseComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(
        topBar = {
            MSTopAppBar(
                title = "Get MobileStack",
                onBackTap = component::onBackTap
            )
        }
    ) { it ->
        LazyColumn(
            modifier = Modifier.padding(it).padding(16.dp)
        ) {
            item {
                if (model.loading && model.products.isEmpty()) {
                    Text("Loading...")
                }
                if (model.products.isEmpty() && !model.loading) {
                    Text("No products available.")
                }
                Spacer(Modifier.height(16.dp))
            }
            items(model.products.sortedBy { it.price }) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(24.dp))
                        val duration = when (val period = product.period) {
                            is Product.Period.Lifetime -> "Once"
                            is Product.Period.Duration -> {
                                val unit =
                                    period.unit.name.lowercase().replaceFirstChar { it.uppercase() }
                                when (period.value) {
                                    1 -> unit
                                    else -> "${period.value} ${unit}s"
                                }
                            }
                        }
                        Text(
                            text = "${product.price} / $duration",
                            style = MaterialTheme.typography.displaySmall,
                        )
                        Spacer(Modifier.height(24.dp))
                        FeatureItem("Kotlin Multiplatform Boilerplate")
                        Spacer(Modifier.height(4.dp))
                        FeatureItem("Auth with Firebase")
                        Spacer(Modifier.height(4.dp))
                        FeatureItem("DB with Firestore")
                        Spacer(Modifier.height(4.dp))
                        FeatureItem("Billing with RevenueCat")
                        Spacer(Modifier.height(4.dp))
                        FeatureItem("Analytics with Firebase")
                        if (product is Product.Starter) {
                            Spacer(Modifier.height(4.dp))
                            NotIncludedItem("ChatGPT Terms/Privacy Policy Prompt")
                            Spacer(Modifier.height(4.dp))
                            NotIncludedItem("Private Discord")
                            Spacer(Modifier.height(4.dp))
                            NotIncludedItem("Lifetime Updates")
                        }
                        if (product is Product.AllIn) {
                            Spacer(Modifier.height(4.dp))
                            FeatureItem("ChatGPT Terms/Privacy Policy Prompt")
                            Spacer(Modifier.height(4.dp))
                            FeatureItem("Private Discord")
                            Spacer(Modifier.height(4.dp))
                            FeatureItem("Lifetime Updates")
                        }
                        Spacer(Modifier.height(32.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Pay once. Build unlimited projects.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        MSFilledButton(
                            text = "Get ${product.title}",
                            onClick = { component.onProductTap(product) },
                            modifier = Modifier.fillMaxWidth(),
                            loading = model.loading
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
