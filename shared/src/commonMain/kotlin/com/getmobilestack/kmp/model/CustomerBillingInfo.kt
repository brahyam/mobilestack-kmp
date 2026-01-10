package com.getmobilestack.kmp.model

data class CustomerBillingInfo(
    val entitlements: List<String>,
    val purchases: List<String>,
    val managementUrl: String?,
)