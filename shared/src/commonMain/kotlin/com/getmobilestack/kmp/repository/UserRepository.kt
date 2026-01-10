package com.getmobilestack.kmp.repository

import com.getmobilestack.kmp.model.User
import com.getmobilestack.kmp.provider.AuthProvider
import com.getmobilestack.kmp.repository.dto.UserDto
import com.getmobilestack.kmp.repository.dto.toDto
import com.getmobilestack.kmp.repository.dto.toModel
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.LocalDate

interface UserRepository {
    suspend fun createUser(
        email: String? = null,
        birthdate: LocalDate? = null,
        marketingConsent: Boolean? = null
    ): User

    suspend fun getUser(): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser()
}

class FirebaseUserRepository(
    private val authProvider: AuthProvider,
    private val firebaseFirestore: FirebaseFirestore,
) : UserRepository {
    override suspend fun createUser(
        email: String?,
        birthdate: LocalDate?,
        marketingConsent: Boolean?
    ): User {
        val id = authProvider.getAuthUser()?.id
            ?: throw IllegalStateException("User needs to be authenticated")
        val userDto = UserDto(
            id = id,
            email = email,
            birthdate = birthdate,
            marketingConsent = marketingConsent ?: false
        )
        firebaseFirestore.collection("users").document(userDto.id).set(userDto)
        return userDto.toModel()
    }

    override suspend fun getUser(): User? {
        val userId = authProvider.getAuthUser()?.id ?: return null
        val doc =
            firebaseFirestore.collection("users").document(userId).get()
        return if (doc.exists) {
            doc.data(UserDto.serializer()).toModel()
        } else {
            null
        }
    }

    override suspend fun updateUser(user: User) {
        val userDto = user.toDto()
        firebaseFirestore.collection("users").document(userDto.id).set(userDto)
    }

    override suspend fun deleteUser() {
        val userId = authProvider.getAuthUser()?.id ?: return
        firebaseFirestore.collection("users").document(userId).delete()
    }
}