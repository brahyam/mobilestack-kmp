package com.zenithapps.mobilestack.android.provider

import android.app.Activity
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.awaitLogOut
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.models.PurchaseState
import com.zenithapps.mobilestack.android.BuildConfig
import com.zenithapps.mobilestack.provider.BillingProvider

class RevenueCatBillingProvider(
    private val activity: Activity
) : BillingProvider {
    override suspend fun configure(apiKey: String, userId: String?) {
        if (BuildConfig.DEBUG) {
            Purchases.logLevel = LogLevel.DEBUG
        } else {
            Purchases.logLevel = LogLevel.ERROR
        }

        Purchases.configure(
            PurchasesConfiguration.Builder(activity, apiKey)
                .appUserID(userId)
                .build()
        )
    }

    override suspend fun logIn(userId: String, email: String?) {
        Purchases.sharedInstance.awaitLogIn(userId)
        Purchases.sharedInstance.setEmail(email)
    }

    override suspend fun setEmail(email: String) {
        Purchases.sharedInstance.setEmail(email)
    }

    override suspend fun logOut() {
        if (!Purchases.sharedInstance.isAnonymous) {
            Purchases.sharedInstance.awaitLogOut()
        }
    }

    override suspend fun getCustomerInfo(): BillingProvider.CustomerInfo {
        val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
        return BillingProvider.CustomerInfo(
            entitlements = customerInfo.entitlements.active.keys.toList(),
            purchases = customerInfo.allPurchasedProductIds.toList(),
            managementUrl = customerInfo.managementURL.toString()
        )
    }

    override suspend fun getProducts(): List<BillingProvider.Product> {
        val offerings = Purchases.sharedInstance.awaitOfferings()
        return offerings.current?.availablePackages?.map {
            BillingProvider.Product(
                id = it.product.id,
                packageId = it.identifier,
                title = it.product.name,
                description = it.product.description,
                price = it.product.price.formatted,
                period = BillingProvider.Period(
                    value = it.product.period?.value ?: 0,
                    unit = it.product.period?.unit?.name ?: "ONCE"
                )
            )
        } ?: emptyList()
    }

    override suspend fun purchase(packageId: String) {
        val offerings = Purchases.sharedInstance.awaitOfferings()
        val packageToPurchase =
            offerings.current?.availablePackages?.find { it.identifier == packageId }
                ?: throw Exception("Product not found")
        val result = Purchases.sharedInstance.awaitPurchase(
            purchaseParams = PurchaseParams.Builder(activity, packageToPurchase)
                .build()
        )
        if (result.customerInfo.entitlements.active.isEmpty()) {
            throw Exception("Purchase failed")
        }
        if (result.storeTransaction.purchaseState != PurchaseState.PURCHASED) {
            throw Exception("Purchase pending")
        }
    }

    override suspend fun restorePurchases() {
        Purchases.sharedInstance.awaitRestore()
    }
}