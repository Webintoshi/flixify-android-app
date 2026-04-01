package com.flixify.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val kryptoniteCode: String,
    val hasActiveSubscription: Boolean = false
)

@Serializable
data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class LoginRequest(
    val code: String,
    val deviceName: String = "Flixify Android",
    val platform: String = "android-native"
)

@Serializable
data class RegisterAnonRequest(
    val deviceName: String = "Flixify Android",
    val platform: String = "android-native",
    val installationId: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@Serializable
data class RegisterAnonResponse(
    val kryptoniteCode: String
)
