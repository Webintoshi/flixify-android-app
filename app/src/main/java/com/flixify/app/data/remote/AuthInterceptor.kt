package com.flixify.app.data.remote

import com.flixify.app.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = runBlocking { sessionManager.getAccessToken() }
        
        val newRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("X-Flixify-Client-Runtime", "native")
                .header("Accept", "application/json")
                .build()
        } else {
            request.newBuilder()
                .header("X-Flixify-Client-Runtime", "native")
                .header("Accept", "application/json")
                .build()
        }
        
        return chain.proceed(newRequest)
    }
}

class TokenRefreshInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // If we get 401/403, try to refresh token
        if (response.code == 401 || response.code == 403) {
            val refreshToken = runBlocking { sessionManager.getRefreshToken() }
            
            if (refreshToken != null) {
                // Close the current response
                response.close()
                
                // Here we would normally call refresh token API
                // For now, just let it fail and UI will handle logout
            }
        }
        
        return response
    }
}
