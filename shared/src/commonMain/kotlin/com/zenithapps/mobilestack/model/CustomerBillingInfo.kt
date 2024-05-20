package com.zenithapps.mobilestack.model

data class CustomerBillingInfo(
    val entitlements: List<String>,
    val purchases: List<String>,
    val managementUrl: String?,
)