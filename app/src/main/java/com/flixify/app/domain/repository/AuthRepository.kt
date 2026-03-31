package com.flixify.app.domain.repository

import com.flixify.app.domain.model.AuthResult
import com.flixify.app.domain.model.AuthToken
import com.flixify.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    val currentUser: Flow<User?>
    
    suspend fun login(code: String): AuthResult<User>
    suspend fun register(): AuthResult<User>
    suspend fun refreshToken(): AuthResult<AuthToken>
    suspend fun logout()
    
    fun getAccessToken(): Flow<String?>
}
