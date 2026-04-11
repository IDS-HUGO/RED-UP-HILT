package com.hugodev.red_up.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.authDataStore

    val tokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val userIdFlow: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    val userNameFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    val fcmTokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[FCM_TOKEN_KEY]
    }

    val deviceUuidFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[DEVICE_UUID_KEY]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun saveUser(id: Long, name: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = id
            prefs[USER_NAME_KEY] = name
        }
    }

    suspend fun saveFcmToken(token: String) {
        dataStore.edit { prefs ->
            prefs[FCM_TOKEN_KEY] = token
        }
    }

    suspend fun getOrCreateDeviceUuid(): String {
        var value: String? = null
        dataStore.edit { prefs ->
            value = prefs[DEVICE_UUID_KEY]
            if (value.isNullOrBlank()) {
                value = UUID.randomUUID().toString()
                prefs[DEVICE_UUID_KEY] = value!!
            }
        }
        return value!!
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_NAME_KEY)
        }
    }

    private companion object {
        val TOKEN_KEY = stringPreferencesKey("access_token")
        val USER_ID_KEY = longPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
        val DEVICE_UUID_KEY = stringPreferencesKey("device_uuid")
    }
}
