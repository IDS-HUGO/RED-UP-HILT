package com.hugodev.red_up.core.sync

import android.content.Context
import android.util.Log
import com.hugodev.red_up.BuildConfig
import com.hugodev.red_up.core.data.AuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object PushTokenRegistrar {
    private const val TAG = "PushTokenRegistrar"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    suspend fun syncNow(
        context: Context,
        reason: String,
        explicitFcmToken: String? = null
    ) = withContext(Dispatchers.IO) {
        val authPreferences = AuthPreferences(context)
        val authToken = authPreferences.tokenFlow.firstOrNull()
        val fcmToken = explicitFcmToken ?: authPreferences.fcmTokenFlow.firstOrNull()

        if (authToken.isNullOrBlank()) {
            Log.d(TAG, "Skipping token sync: no auth token. reason=$reason")
            return@withContext
        }
        if (fcmToken.isNullOrBlank()) {
            Log.d(TAG, "Skipping token sync: no fcm token. reason=$reason")
            return@withContext
        }

        val deviceUuid = authPreferences.getOrCreateDeviceUuid()
        val registerPayload = JSONObject()
            .put("uuid_dispositivo", deviceUuid)
            .put("plataforma", "android")
            .put("token_push", fcmToken)
            .toString()
        val updatePayload = JSONObject()
            .put("uuid_dispositivo", deviceUuid)
            .put("token_push", fcmToken)
            .toString()

        val registerCode = postJson(
            path = "api/notificaciones/dispositivos",
            bearerToken = authToken,
            payload = registerPayload
        )
        val updateCode = postJson(
            path = "api/notificaciones/dispositivos/token",
            bearerToken = authToken,
            payload = updatePayload,
            method = "PUT"
        )

        Log.d(
            TAG,
            "Token sync done. reason=$reason registerCode=$registerCode updateCode=$updateCode"
        )
    }

    private fun postJson(
        path: String,
        bearerToken: String,
        payload: String,
        method: String = "POST"
    ): Int {
        val url = "${BuildConfig.BASE_URL_UPRED}$path"
        val requestBody = payload.toRequestBody(jsonMediaType)
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $bearerToken")
            .addHeader("Content-Type", "application/json")

        val request = if (method == "PUT") {
            requestBuilder.put(requestBody).build()
        } else {
            requestBuilder.post(requestBody).build()
        }

        return try {
            client.newCall(request).execute().use { response -> response.code }
        } catch (e: Exception) {
            Log.e(TAG, "Token sync request failed. path=$path", e)
            -1
        }
    }
}
