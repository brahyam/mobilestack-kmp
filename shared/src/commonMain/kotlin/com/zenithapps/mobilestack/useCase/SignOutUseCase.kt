package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.util.Result
import io.github.aakira.napier.Napier

class SignOutUseCase(
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke() {
        try {
            authProvider.signOut()
            billingProvider.logOut()
            Result.Success(Unit)
        } catch (exception: Exception) {
            Napier.e(exception) { "Sign out failed" }
            throw SignOutError(exception.message ?: "Unknown error")
        }
    }

    data class SignOutError(val reason: String) : Exception(reason)
}