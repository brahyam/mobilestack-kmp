package com.zenithapps.mobilestack.provider

interface BillingProvider {
    suspend fun configure(apiKey: String, userId: String?)
    suspend fun logIn(userId: String, email: String?)
    suspend fun setEmail(email: String)
    suspend fun logOut()
    suspend fun getCustomerInfo(): CustomerInfo
    suspend fun getProducts(): List<Product>
    suspend fun purchase(packageId: String)
    suspend fun restorePurchases()

    data class CustomerInfo(
        val entitlements: List<String>,
        val purchases: List<String>,
        val managementUrl: String?,
    )

    data class Product(
        val id: String,
        val packageId: String,
        val title: String,
        val description: String,
        val price: String,
        val period: Period
    )

    data class Period(
        val value: Int,
        val unit: String //TODO: move to enum
    )
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

    override suspend fun getCustomerInfo(): BillingProvider.CustomerInfo {
        return BillingProvider.CustomerInfo(
            entitlements = emptyList(),
            purchases = emptyList(),
            managementUrl = null
        )
    }

    override suspend fun getProducts(): List<BillingProvider.Product> {
        return emptyList()
    }

    override suspend fun purchase(packageId: String) {
        // no-op
    }

    override suspend fun restorePurchases() {
        // no-op
    }
}