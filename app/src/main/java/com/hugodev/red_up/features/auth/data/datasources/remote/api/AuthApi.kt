package com.hugodev.red_up.features.auth.data.datasources.remote.api

import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthLoginRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthRegisterRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthTokenDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthUserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthLoginRequestDto): AuthTokenDto

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRegisterRequestDto): AuthUserDto

    @GET("api/auth/me")
    suspend fun me(): AuthUserDto
}
