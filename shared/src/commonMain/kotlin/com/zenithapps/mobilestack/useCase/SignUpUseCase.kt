package com.zenithapps.mobilestack.useCase

import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.repository.UserRepository
import com.zenithapps.mobilestack.util.Result

class SignUpUseCase(
    private val authProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val billingProvider: BillingProvider
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        marketingConsent: Boolean
    ): Result<Unit, SignUpWithEmailError> {
        return try {
            val authUser = authProvider.signUpWithEmailPassword(email, password)
            val user = userRepository.createUser(authUser.id, authUser.email, marketingConsent)
            billingProvider.logIn(user.id, user.email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SignUpWithEmailError.fromException(e))
        }
    }

    suspend fun anonymously(): Result<Unit, SignUpAnonError> {
        return try {
            val authUser = authProvider.signUpAnonymously()
            val user = userRepository.createUser(authUser.id, authUser.email, false)
            billingProvider.logIn(user.id, user.email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SignUpAnonError(e.message ?: "Unknown error"))
        }
    }

    data class SignUpAnonError(val reason: String)

    sealed class SignUpWithEmailError {
        data object EmailAlreadyExists : SignUpWithEmailError()
        data object InvalidEmail : SignUpWithEmailError()
        data object InvalidPassword : SignUpWithEmailError()
        data class Other(val reason: String) : SignUpWithEmailError()

        companion object {
            fun fromException(exception: Exception): SignUpWithEmailError {
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