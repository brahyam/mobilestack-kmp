package com.getmobilestack.kmp.provider

import com.russhwolf.settings.Settings

interface KeyValueStorageProvider {
    fun setString(key: String, value: String)
    fun getString(key: String): String?

    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean?

    fun setInt(key: String, value: Int)
    fun getInt(key: String): Int?

    fun remove(key: String)
    fun clearStore()
}

class KMPSettingsProvider(
    private val settings: Settings = Settings()
) : KeyValueStorageProvider {
    override fun setString(key: String, value: String) {
        settings.putString(key, value)
    }

    override fun getString(key: String): String? {
        return settings.getStringOrNull(key)
    }

    override fun setBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    override fun getBoolean(key: String): Boolean? {
        return settings.getBooleanOrNull(key)
    }

    override fun setInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    override fun getInt(key: String): Int? {
        return settings.getIntOrNull(key)
    }

    override fun remove(key: String) {
        settings.remove(key)
    }

    override fun clearStore() {
        settings.clear()
    }
}



