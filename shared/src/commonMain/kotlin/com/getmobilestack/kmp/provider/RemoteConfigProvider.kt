package com.getmobilestack.kmp.provider

import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig

interface RemoteConfigProvider {

    suspend fun setInterval(seconds: Long)
    suspend fun fetchAndActivate()
    fun getBoolean(key: String): Boolean
    fun getDouble(key: String): Double
    fun getLong(key: String): Long
    fun getString(key: String): String

}

private const val MINIMUM_FETCH_INTERVAL_SECONDS = 3600L

class FirebaseRemoteConfigProvider(
    private val remoteConfig: FirebaseRemoteConfig
) : RemoteConfigProvider {
    override suspend fun setInterval(seconds: Long) {
        remoteConfig.settings { minimumFetchIntervalInSeconds = seconds }
    }

    override suspend fun fetchAndActivate() {
        remoteConfig.settings { minimumFetchIntervalInSeconds = MINIMUM_FETCH_INTERVAL_SECONDS }
        remoteConfig.fetchAndActivate()
    }

    override fun getBoolean(key: String): Boolean {
        return remoteConfig.getValue(key).asBoolean()
    }

    override fun getDouble(key: String): Double {
        return remoteConfig.getValue(key).asDouble()
    }

    override fun getLong(key: String): Long {
        return remoteConfig.getValue(key).asLong()
    }

    override fun getString(key: String): String {
        return remoteConfig.getValue(key).asString()
    }
}

