package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.util.Result

class SignInUseCase(
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke(
        email: String, password: String
    ): Result<Unit, SignInError> {
        return try {
            val authUser = authProvider.signInWithEmailPassword(email, password)
            billingProvider.logIn(authUser.id, authUser.email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SignInError.fromException(e))
        }
    }


    sealed class SignInError {
        data object InvalidEmail : SignInError()
        data object InvalidCredentials : SignInError()
        data class Other(val reason: String) : SignInError()

        companion object {
            fun fromException(exception: Exception): SignInError {
                return when {
                    exception.message == null -> Other("Unknown error")
                    exception.message!!.contains("email", true) -> InvalidEmail
                    exception.message!!.contains("credential", true) -> InvalidCredentials
                    else -> Other(exception.message!!)
                }
            }
        }
    }
}
