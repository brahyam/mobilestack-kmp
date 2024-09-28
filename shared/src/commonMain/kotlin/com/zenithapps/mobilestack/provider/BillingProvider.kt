package com.zenithapps.mobilestack.provider

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitSyncPurchases
import com.zenithapps.mobilestack.model.CustomerBillingInfo
import com.zenithapps.mobilestack.model.Product
import io.github.aakira.napier.Napier

const val REVENUE_CAT_ANDROID_API_KEY = "REVCAT_API_KEY_ANDROID"

const val REVENUE_CAT_IOS_API_KEY = "REVCAT_API_KEY_IOS"

interface BillingProvider {
    val isConfigured: Boolean
    suspend fun configure(apiKey: String, userId: String?)
    suspend fun logIn(userId: String, email: String?)
    suspend fun setEmail(email: String)
    suspend fun logOut()
    suspend fun getCustomerBillingInfo(): CustomerBillingInfo
    suspend fun getProducts(): List<Product>
    suspend fun purchase(packageId: String)
    suspend fun restorePurchases()
}

class KMPRevenueCatBillingProvider : BillingProvider {
    private var configured: Boolean = false
    override val isConfigured: Boolean
        get() = configured
    override suspend fun configure(apiKey: String, userId: String?) {
        if (apiKey.isNotBlank()) {
            try {
                val purchasesConfig = PurchasesConfiguration(apiKey) {
                    appUserId = userId
                }
                Purchases.logLevel = LogLevel.DEBUG
                Purchases.configure(purchasesConfig)
                configured = true
            } catch (e: Exception) {
                Napier.e(e) { "Failed to configure Billing Provider." }
            }
        } else {
            Napier.w { "Billing Provider not configured. API key was blank  " }
        }
    }

    override suspend fun logIn(userId: String, email: String?) {
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            return
        }
        Purchases.sharedInstance.awaitLogIn(newAppUserID = userId)
        if (email != null) {
            Purchases.sharedInstance.setAttributes(mapOf("email" to email))
        }
    }

    override suspend fun setEmail(email: String) {
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            return
        }
        Purchases.sharedInstance.setAttributes(mapOf("email" to email))
    }

    override suspend fun logOut() {
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            return
        }
        Purchases.sharedInstance.awaitLogOut()
    }

    override suspend fun getCustomerBillingInfo(): CustomerBillingInfo {
        return if (!configured) {
            Napier.w { "Billing Provider not configured." }
            CustomerBillingInfo(emptyList(), emptyList(), null)
        } else {
            val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
            val entitlements = customerInfo.entitlements.all.map { it.key }
            val purchases = emptyList<String>() // find out how to map purchases
            val managementUrl = customerInfo.managementUrlString
            CustomerBillingInfo(entitlements, purchases, managementUrl)
        }
    }

    override suspend fun getProducts(): List<Product> {
        return emptyList()
    }

    override suspend fun purchase(packageId: String) {
        //  no-op
    }

    override suspend fun restorePurchases() {
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            return
        } else {
            Purchases.sharedInstance.awaitSyncPurchases()
        }
    }
}