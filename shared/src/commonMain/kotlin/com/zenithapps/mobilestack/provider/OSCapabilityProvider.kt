package com.zenithapps.mobilestack.provider

interface OSCapabilityProvider {
    fun openUrl(url: String)

    fun getPlatform(): Platform

    fun getAppVersion(): String

    fun managePurchases()

    fun openAppSettings()

    fun requestStoreReview()

    fun shareImage(imageByteArray: ByteArray, mimeType: String, title: String, message: String)

    enum class Platform {
        ANDROID,
        IOS
    }
}