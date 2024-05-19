package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.repository.UserRepository
import com.zenithapps.mobilestack.util.Result
import io.github.aakira.napier.Napier

class SignUpUseCase(
    private val authProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        marketingConsent: Boolean,
        purchasePending: Boolean = false
    ) {
        try {
            if (email.isBlank() || password.isBlank()) {
                throw SignUpWithEmailException.EmptyEmailOrPassword
            }
            val authUser = authProvider.signUpWithEmailPassword(email, password)
            val user = userRepository.createUser(
                authUser.id,
                authUser.email,
                marketingConsent,
                purchasePending
            )
            billingProvider.logIn(user.id, user.email)
        } catch (e: Exception) {
            Napier.e(e) { "Sign up failed" }
            throw SignUpWithEmailException.fromException(e)
        }
    }

    suspend fun anonymously(): Result<Unit, SignUpAnonException> {
        return try {
            val authUser = authProvider.signUpAnonymously()
            val user = userRepository.createUser(
                authUser.id,
                authUser.email,
                marketingConsent = false,
                purchasePending = false
            )
            billingProvider.logIn(user.id, user.email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SignUpAnonException(e.message ?: "Unknown error"))
        }
    }

    data class SignUpAnonException(val reason: String) : Exception()

    sealed class SignUpWithEmailException : Exception() {
        data object EmailAlreadyExists : SignUpWithEmailException()
        data object InvalidEmail : SignUpWithEmailException()
        data object InvalidPassword : SignUpWithEmailException()
        data object EmptyEmailOrPassword : SignUpWithEmailException()
        data class Other(val reason: String) : SignUpWithEmailException()

        companion object {
            fun fromException(exception: Exception): SignUpWithEmailException {
                return when {
                    exception.message == null -> Other("Unknown error")
                    exception.message!!.contains("already", true) -> EmailAlreadyExists
                    exception.message!!.contains("email", true) -> InvalidEmail
                    exception.message!!.contains("password", true) -> InvalidPassword
                    else -> Other(exception.message!!)
                }
            }
        }
    }
}