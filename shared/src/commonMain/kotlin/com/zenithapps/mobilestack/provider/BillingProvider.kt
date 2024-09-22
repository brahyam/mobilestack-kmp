package com.zenithapps.mobilestack.provider

import com.mmk.kmprevenuecat.purchases.Purchases
import com.zenithapps.mobilestack.model.CustomerBillingInfo
import com.zenithapps.mobilestack.model.Product
import io.github.aakira.napier.Napier
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
            Purchases.configure(apiKey, userId)
            configured = true
        } else {
            Napier.w { "Billing Provider not configured." }
        }
    }

    override suspend fun logIn(userId: String, email: String?) = suspendCoroutine { continuation ->
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            continuation.resumeWith(Result.success(Unit))
        } else {
            Purchases.login(userId) {
                it.onSuccess {
                    Purchases.setAttributes(mapOf("email" to email))
                    continuation.resumeWith(Result.success(Unit))
                }
                it.onFailure { error ->
                    continuation.resumeWithException(error)
                }
            }
        }
    }

    override suspend fun setEmail(email: String) {
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            return
        }
        Purchases.setAttributes(mapOf("email" to email))
    }

    override suspend fun logOut() = suspendCoroutine { continuation ->
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            continuation.resumeWith(Result.success(Unit))
        } else {
            Purchases.logOut {
                it.onSuccess {
                    continuation.resumeWith(Result.success(Unit))
                }
                it.onFailure { error ->
                    continuation.resumeWithException(error)
                }
            }
        }
    }

    override suspend fun getCustomerBillingInfo(): CustomerBillingInfo =
        suspendCoroutine { continuation ->
            if (!configured) {
                Napier.w { "Billing Provider not configured." }
                continuation.resumeWith(
                    Result.success(
                        CustomerBillingInfo(
                            emptyList(),
                            emptyList(),
                            null
                        )
                    )
                )
            } else {
                Purchases.getCustomerInfo {
                    it.onSuccess { customerInfo ->
                        val entitlements = customerInfo.entitlements.all.map { entitlement ->
                            entitlement.key
                        }
                        val purchases = emptyList<String>() // find out how to map purchases
                        val managementUrl = customerInfo.managementURL
                        continuation.resumeWith(
                            Result.success(
                                CustomerBillingInfo(
                                    entitlements,
                                    purchases,
                                    managementUrl
                                )
                            )
                        )
                    }
                    it.onFailure { error ->
                        continuation.resumeWithException(error)
                    }
                }
            }
        }

    override suspend fun getProducts(): List<Product> {
        return emptyList()
    }

    override suspend fun purchase(packageId: String) {
        //  no-op
    }

    override suspend fun restorePurchases() = suspendCoroutine { continuation ->
        if (!configured) {
            Napier.w { "Billing Provider not configured." }
            continuation.resumeWith(Result.success(Unit))
        } else {
            Purchases.syncPurchases {
                it.onSuccess {
                    continuation.resumeWith(Result.success(Unit))
                }
                it.onFailure { error ->
                    continuation.resumeWithException(error)
                }
            }
        }
    }
}