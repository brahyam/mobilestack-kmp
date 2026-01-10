package com.getmobilestack.kmp.provider

interface OSCapabilityProvider {
    fun openUrl(url: String)

    fun getPlatform(): Platform

    fun getAppVersion(): String

    fun managePurchases()

    fun openAppSettings()

    fun requestStoreReview()

    fun shareImage(imageByteArray: ByteArray, mimeType: String, title: String, message: String)

    fun vibrate(durationMs: Long, strength: VibrationStrength)

    enum class VibrationStrength {
        LIGHT,
        MEDIUM,
        STRONG
    }

    enum class Platform {
        ANDROID,
        IOS
    }
}