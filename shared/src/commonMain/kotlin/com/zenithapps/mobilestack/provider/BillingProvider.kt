package com.zenithapps.mobilestack.provider

import com.zenithapps.mobilestack.model.CustomerBillingInfo
import com.zenithapps.mobilestack.model.Product

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

class MockBillingProvider : BillingProvider {
    override suspend fun configure(apiKey: String, userId: String?) {
        // no-op
    }

    override suspend fun logIn(userId: String, email: String?) {
        // no-op
    }

    override suspend fun setEmail(email: String) {
        // no-op
    }

    override suspend fun logOut() {
        // no-op
    }

    override suspend fun getCustomerBillingInfo(): CustomerBillingInfo {
        return CustomerBillingInfo(
            entitlements = emptyList(),
            purchases = emptyList(),
            managementUrl = null
        )
    }

    override suspend fun getProducts(): List<Product> {
        return emptyList()
    }

    override suspend fun purchase(packageId: String) {
        // no-op
    }

    override suspend fun restorePurchases() {
        // no-op
    }
}