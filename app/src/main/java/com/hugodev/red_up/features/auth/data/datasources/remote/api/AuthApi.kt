package com.hugodev.red_up.features.auth.data.datasources.remote.api

import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthLoginRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthRegisterRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthTokenDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthUserDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordConfirmRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthLoginRequestDto): AuthTokenDto

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRegisterRequestDto): AuthUserDto

    @POST("api/auth/forgot-password/request")
    suspend fun requestPasswordReset(@Body request: ForgotPasswordRequestDto): ForgotPasswordResponseDto

    @POST("api/auth/forgot-password/confirm")
    suspend fun confirmPasswordReset(@Body request: ForgotPasswordConfirmRequestDto): ForgotPasswordResponseDto

    @GET("api/auth/me")
    suspend fun me(): AuthUserDto
}
