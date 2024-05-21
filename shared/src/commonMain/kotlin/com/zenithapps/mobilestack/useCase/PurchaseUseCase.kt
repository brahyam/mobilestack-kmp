package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.model.Product
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.repository.UserRepository
import io.github.aakira.napier.Napier

class PurchaseUseCase(
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider,
    private val userRepository: UserRepository,
    private val signUp: SignUpUseCase
) {
    suspend operator fun invoke(product: Product) {
        try {
            // Needs to be signed in to assign the purchase to the correct user
            if (!authProvider.isLoggedIn()) {
                signUp.anonymously()
            }
            billingProvider.purchase(product.packageId)
        } catch (exception: Exception) {
            Napier.e(exception) { "Purchase failed" }
            val purchaseException = PurchaseException.fromException(exception)
            throw purchaseException
        }
    }

    sealed class PurchaseException(reason: String) : Exception(reason) {
        data object Pending : PurchaseException("Purchase pending")
        data object Declined : PurchaseException("Purchase declined")
        data object Cancelled : PurchaseException("Purchase cancelled")
        data class Other(val reason: String) : PurchaseException(reason)
        companion object {
            fun fromException(exception: Exception): PurchaseException {
                return when {
                    exception.message?.contains("pending") == true -> Pending
                    exception.message?.contains("not allowed") == true -> Declined
                    exception.message?.contains("cancelled") == true -> Cancelled
                    else -> Other(exception.message ?: "Unknown error")
                }
            }
        }
    }
}
