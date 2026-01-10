package com.getmobilestack.kmp.useCase

import com.getmobilestack.kmp.provider.AuthProvider
import com.getmobilestack.kmp.provider.BillingProvider
import io.github.aakira.napier.Napier

class SignInUseCase(
    private val authProvider: AuthProvider,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke(email: String, password: String) {
        try {
            if (email.isBlank() || password.isBlank()) {
                throw SignInException.EmptyEmailOrPassword
            }
            val authUser = authProvider.signInWithEmailPassword(email, password)
            billingProvider.logIn(authUser.id, authUser.email)
        } catch (exception: Exception) {
            Napier.e(exception) { "Sign in failed" }
            throw SignInException.fromException(exception)
        }
    }


    sealed class SignInException(reason: String) : Exception(reason) {
        data object EmptyEmailOrPassword : SignInException("Empty email or password")
        data object InvalidEmail : SignInException("Invalid email")
        data object InvalidCredentials : SignInException("Invalid credentials")
        data class Other(val reason: String) : SignInException(reason)

        companion object {
            fun fromException(exception: Exception): SignInException {
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
