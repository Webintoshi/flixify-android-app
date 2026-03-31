package com.flixify.app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val code: String,
    val deviceName: String = "Flixify Android",
    val platform: String = "android-native",
    val installationId: String
)

@Serializable
data class RegisterRequest(
    val deviceName: String = "Flixify Android",
    val platform: String = "android-native",
    val installationId: String
)

@Serializable
data class AuthResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    val user: User? = null
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@Serializable
data class User(
    val id: String,
    val kryptoniteCode: String? = null,
    val code: String? = null,
    val hasActiveSubscription: Boolean = false,
    val status: String? = null,
    val activePackage: Package? = null,
    val hasAssignedLink: Boolean = false
)

@Serializable
data class Package(
    val id: String,
    val title: String,
    val durationMonths: Int,
    val remainingDays: Int? = null
)
