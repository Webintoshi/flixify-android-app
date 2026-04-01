package com.flixify.app.data.remote

import com.flixify.app.domain.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register-anon")
    suspend fun registerAnon(@Body request: RegisterAnonRequest): Response<RegisterAnonResponse>
    
    @POST("auth/login-by-code")
    suspend fun loginByCode(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LoginResponse>
}
