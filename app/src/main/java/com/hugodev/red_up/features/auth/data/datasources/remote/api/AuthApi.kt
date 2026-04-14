package com.hugodev.red_up.features.auth.data.datasources.remote.api

import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthLoginRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthTokenDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthUserDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordConfirmRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthLoginRequestDto): AuthTokenDto

    @Multipart
    @POST("api/auth/register")
    suspend fun register(
        @Part("correo_institucional") correoInstitucional: RequestBody,
        @Part("nombre") nombre: RequestBody,
        @Part("apellido_paterno") apellidoPaterno: RequestBody,
        @Part("apellido_materno") apellidoMaterno: RequestBody?,
        @Part("fecha_nacimiento") fechaNacimiento: RequestBody,
        @Part("password") password: RequestBody,
        @Part fotoPerfil: MultipartBody.Part? = null
    ): AuthUserDto

    @POST("api/auth/forgot-password/request")
    suspend fun requestPasswordReset(@Body request: ForgotPasswordRequestDto): ForgotPasswordResponseDto

    @POST("api/auth/forgot-password/confirm")
    suspend fun confirmPasswordReset(@Body request: ForgotPasswordConfirmRequestDto): ForgotPasswordResponseDto

    @GET("api/auth/me")
    suspend fun me(): AuthUserDto
}
