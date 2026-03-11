package com.hugodev.red_up.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricCredentialStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(email: String, password: String) {
        securePrefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun getCredentials(): Pair<String, String>? {
        val email = securePrefs.getString(KEY_EMAIL, null)
        val password = securePrefs.getString(KEY_PASSWORD, null)
        return if (email.isNullOrBlank() || password.isNullOrBlank()) {
            null
        } else {
            email to password
        }
    }

    fun hasCredentials(): Boolean = getCredentials() != null

    fun clearCredentials() {
        securePrefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "biometric_secure_auth"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
    }
}
