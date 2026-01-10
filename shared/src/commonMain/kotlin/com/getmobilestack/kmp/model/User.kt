package com.getmobilestack.kmp.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String?,
    val birthdate: LocalDate?,
    val marketingConsent: Boolean
)