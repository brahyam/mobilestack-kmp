package com.getmobilestack.kmp.repository.dto

import com.getmobilestack.kmp.model.User
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String?,
    val birthdate: LocalDate?,
    val marketingConsent: Boolean
)

fun UserDto.toModel(): User = User(
    id = id,
    email = email,
    birthdate = birthdate,
    marketingConsent = marketingConsent
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    email = email,
    birthdate = birthdate,
    marketingConsent = marketingConsent
)
