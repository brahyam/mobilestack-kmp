package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.repository.UserRepository
import io.github.aakira.napier.Napier

class SignUpUseCase(
    private val authProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        marketingConsent: Boolean
    ) {
        try {
            if (email.isBlank() || password.isBlank()) {
                throw SignUpWithEmailException.EmptyEmailOrPassword
            }
            val authUser = authProvider.signUpWithEmailPassword(email, password)
            val user = userRepository.createUser(
                authUser.id,
                authUser.email,
                marketingConsent
            )
            billingProvider.logIn(user.id, user.email)
        } catch (e: Exception) {
            Napier.e(e) { "Sign up failed" }
            throw SignUpWithEmailException.fromException(e)
        }
    }

    suspend fun anonymously() {
        return try {
            val authUser = authProvider.signUpAnonymously()
            val user = userRepository.createUser(
                authUser.id,
                authUser.email,
                marketingConsent = false
            )
            billingProvider.logIn(user.id, user.email)
        } catch (e: Exception) {
            throw SignUpAnonException(e.message ?: "Unknown error")
        }
    }

    data class SignUpAnonException(val reason: String) : Exception(reason)

    sealed class SignUpWithEmailException(reason: String) : Exception(reason) {
        data object EmailAlreadyExists : SignUpWithEmailException("Email already exists")
        data object InvalidEmail : SignUpWithEmailException("Invalid email")
        data object InvalidPassword : SignUpWithEmailException("Invalid password")
        data object EmptyEmailOrPassword : SignUpWithEmailException("Empty email or password")
        data class Other(val reason: String) : SignUpWithEmailException(reason)

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