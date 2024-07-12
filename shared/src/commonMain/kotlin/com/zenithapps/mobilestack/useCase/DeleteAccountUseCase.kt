package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.repository.UserRepository
import com.zenithapps.mobilestack.util.Result
import io.github.aakira.napier.Napier

class DeleteAccountUseCase(
    private val userRepository: UserRepository,
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke() {
        try {
            val userId = authProvider.getAuthUser()!!.id
            userRepository.deleteUser(userId)
            authProvider.deleteAccount()
            billingProvider.logOut()
            Result.Success(Unit)
        } catch (exception: Exception) {
            Napier.e(exception) { "Delete account failed" }
            throw DeleteAccountError(exception.message ?: "Unknown error")
        }
    }

    data class DeleteAccountError(val reason: String) : Exception(reason)
}