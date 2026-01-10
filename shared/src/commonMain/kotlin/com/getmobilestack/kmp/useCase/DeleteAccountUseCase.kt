package com.getmobilestack.kmp.useCase

import com.getmobilestack.kmp.provider.AuthProvider
import com.getmobilestack.kmp.provider.BillingProvider
import com.getmobilestack.kmp.repository.UserRepository
import com.getmobilestack.kmp.util.Result
import io.github.aakira.napier.Napier

class DeleteAccountUseCase(
    private val userRepository: UserRepository,
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke() {
        try {
            authProvider.deleteAccount()
            billingProvider.logOut()
            userRepository.deleteUser()
            Result.Success(Unit)
        } catch (exception: Exception) {
            Napier.e(exception) { "Delete account failed" }
            throw DeleteAccountError(exception.message ?: "Unknown error")
        }
    }

    data class DeleteAccountError(val reason: String) : Exception(reason)
}