package com.example.app

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class KeyStore(private val context: Context? = null) {
    private val prefs by lazy {
        val ctx = context ?: throw IllegalStateException("Context required")
        val masterKey = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
            ctx,
            "api_keys",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveKey(name: String, key: String) {
        prefs.edit().putString(name, key).apply()
    }

    fun getKey(name: String): String? = prefs.getString(name, null)
}
