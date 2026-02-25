package com.hugodev.red_up.features.auth.domain.entities

data class AuthUser(
    val id: Long,
    val email: String,
    val fullName: String,
    val avatarUrl: String? = null
)
