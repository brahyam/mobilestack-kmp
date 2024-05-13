package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.util.Result

class SignOutUseCase(
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke(): Result<Unit, SignOutError> {
        return try {
            authProvider.signOut()
            billingProvider.logOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SignOutError(e.message ?: "Unknown error"))
        }
    }

    data class SignOutError(val reason: String)
}