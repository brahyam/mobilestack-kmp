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
import com.zenithapps.mobilestack.model.CustomerBillingInfo
import com.zenithapps.mobilestack.model.Product
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

    override suspend fun getCustomerBillingInfo(): CustomerBillingInfo {
        val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
        return CustomerBillingInfo(
            entitlements = customerInfo.entitlements.active.keys.toList(),
            purchases = customerInfo.allPurchasedProductIds.toList(),
            managementUrl = customerInfo.managementURL.toString()
        )
    }

    override suspend fun getProducts(): List<Product> {
        val offerings = Purchases.sharedInstance.awaitOfferings()
        return offerings.current?.availablePackages?.map {
            when (it.identifier) {
                Product.Starter.ID -> Product.Starter(
                    id = it.product.id,
                    title = it.product.name,
                    description = it.product.description,
                    price = it.product.price.formatted
                )

                Product.AllIn.ID -> Product.AllIn(
                    id = it.product.id,
                    title = it.product.name,
                    description = it.product.description,
                    price = it.product.price.formatted
                )

                else -> Product.Other(
                    id = it.product.id,
                    packageId = it.identifier,
                    title = it.product.name,
                    description = it.product.description,
                    price = it.product.price.formatted,
                    period = when (it.product.period?.value) {
                        0 -> Product.Period.Lifetime
                        else -> Product.Period.Duration(
                            it.product.period!!.value,
                            Product.PeriodUnit.valueOf(it.product.period!!.unit.name)
                        )
                    }
                )
            }
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