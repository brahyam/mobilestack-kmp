package com.zenithapps.mobilestack.provider

import com.mmk.kmprevenuecat.purchases.Purchases
import com.zenithapps.mobilestack.model.CustomerBillingInfo
import com.zenithapps.mobilestack.model.Product
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val REVENUE_CAT_ANDROID_API_KEY = "REVCAT_API_KEY_ANDROID"

const val REVENUE_CAT_IOS_API_KEY = "REVCAT_API_KEY_IOS"

interface BillingProvider {
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
    override suspend fun configure(apiKey: String, userId: String?) {
        Purchases.configure(apiKey, userId)
    }

    override suspend fun logIn(userId: String, email: String?) = suspendCoroutine { continuation ->
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

    override suspend fun setEmail(email: String) {
        Purchases.setAttributes(mapOf("email" to email))
    }

    override suspend fun logOut() = suspendCoroutine { continuation ->
        Purchases.logOut {
            it.onSuccess {
                continuation.resumeWith(Result.success(Unit))
            }
            it.onFailure { error ->
                continuation.resumeWithException(error)
            }
        }
    }

    override suspend fun getCustomerBillingInfo(): CustomerBillingInfo =
        suspendCoroutine { continuation ->
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

    override suspend fun getProducts(): List<Product> {
        return emptyList()
    }

    override suspend fun purchase(packageId: String) {
        //  no-op
    }

    override suspend fun restorePurchases() = suspendCoroutine { continuation ->
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