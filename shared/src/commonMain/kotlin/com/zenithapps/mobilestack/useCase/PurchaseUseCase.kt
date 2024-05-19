package com.zenithapps.mobilestack.useCase

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
    suspend operator fun invoke(packageId: String) {
        try {
            // Needs to be signed in to assign the purchase to the correct user
            if (!authProvider.isLoggedIn()) {
                signUp.anonymously()
            }
            billingProvider.purchase(packageId)
        } catch (exception: Exception) {
            Napier.e(exception) { "Purchase failed" }
            val purchaseException = PurchaseException.fromException(exception)
            if (purchaseException is PurchaseException.Pending) {
                val userId =
                    authProvider.getAuthUser()?.id ?: throw PurchaseException.UserNotSignedIn
                val user = userRepository.getUser(userId) ?: throw PurchaseException.UserNotFound
                userRepository.updateUser(user.copy(purchasePending = true))
            }
            throw purchaseException
        }
    }

    sealed class PurchaseException(reason: String) : Exception(reason) {
        data object UserNotSignedIn : PurchaseException("User not signed in")
        data object UserNotFound : PurchaseException("User not found")
        data object Pending : PurchaseException("Purchase pending")
        data object Declined : PurchaseException("Purchase declined")
        data class Other(val reason: String) : PurchaseException(reason)
        companion object {
            fun fromException(exception: Exception): PurchaseException {
                return when {
                    exception.message?.contains("pending") == true -> Pending
                    exception.message?.contains("not allowed") == true -> Declined
                    else -> Other(exception.message ?: "Unknown error")
                }
            }
        }
    }
}
