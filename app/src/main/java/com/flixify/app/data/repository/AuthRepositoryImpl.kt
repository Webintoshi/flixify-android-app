package com.flixify.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.flixify.app.data.remote.ApiService
import com.flixify.app.domain.model.AuthResult
import com.flixify.app.domain.model.AuthToken
import com.flixify.app.domain.model.LoginRequest
import com.flixify.app.domain.model.RegisterAnonRequest
import com.flixify.app.domain.model.User
import com.flixify.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : AuthRepository {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val INSTALLATION_ID = stringPreferencesKey("installation_id")
    }
    
    override val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN] != null
    }
    
    override val currentUser: Flow<User?> = dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN]?.let {
            User(id = "", kryptoniteCode = "", hasActiveSubscription = false)
        }
    }
    
    override suspend fun login(code: String): AuthResult<User> {
        return try {
            val request = LoginRequest(code = code.replace(" ", ""))
            val response = apiService.loginByCode(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    dataStore.edit { prefs ->
                        prefs[ACCESS_TOKEN] = body.accessToken
                        prefs[REFRESH_TOKEN] = body.refreshToken
                    }
                    AuthResult.Success(body.user)
                } else {
                    AuthResult.Error("Empty response")
                }
            } else {
                AuthResult.Error("Login failed: ${response.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun register(): AuthResult<User> {
        return try {
            val installationId = UUID.randomUUID().toString()
            
            val request = RegisterAnonRequest(installationId = installationId)
            val response = apiService.registerAnon(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val user = User(
                        id = installationId,
                        kryptoniteCode = body.kryptoniteCode,
                        hasActiveSubscription = false
                    )
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Empty response")
                }
            } else {
                AuthResult.Error("Register failed: ${response.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun refreshToken(): AuthResult<AuthToken> {
        return try {
            val currentToken = dataStore.data.map { it[REFRESH_TOKEN] }.let { flow ->
                var token: String? = null
                flow.collect { token = it }
                token
            }
            
            if (currentToken == null) {
                return AuthResult.Error("No refresh token")
            }
            
            val response = apiService.refreshToken(currentToken)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    dataStore.edit { prefs ->
                        prefs[ACCESS_TOKEN] = body.accessToken
                        prefs[REFRESH_TOKEN] = body.refreshToken
                    }
                    AuthResult.Success(AuthToken(body.accessToken, body.refreshToken))
                } else {
                    AuthResult.Error("Empty response")
                }
            } else {
                AuthResult.Error("Token refresh failed: ${response.code()}")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
        }
    }
    
    override fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { it[ACCESS_TOKEN] }
    }
}
