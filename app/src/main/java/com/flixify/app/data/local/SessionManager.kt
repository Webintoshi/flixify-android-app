package com.flixify.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.flixify.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore
    
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val INSTALLATION_ID = stringPreferencesKey("installation_id")
        val USER_DATA = stringPreferencesKey("user_data")
        val SUPPRESSED_UPDATE_VERSION = stringPreferencesKey("suppressed_update_version")
    }
    
    suspend fun saveSession(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }
    
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(USER_DATA)
        }
    }
    
    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[ACCESS_TOKEN]
    }
    
    suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[REFRESH_TOKEN]
    }
    
    val accessTokenFlow: Flow<String?> = dataStore.data.map { it[ACCESS_TOKEN] }
    
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { 
        it[ACCESS_TOKEN] != null 
    }
    
    suspend fun getInstallationId(): String {
        val existing = dataStore.data.first()[INSTALLATION_ID]
        return existing ?: java.util.UUID.randomUUID().toString().also { newId ->
            dataStore.edit { it[INSTALLATION_ID] = newId }
        }
    }
    
    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[USER_DATA] = Json.encodeToString(user)
        }
    }
    
    suspend fun getUser(): User? {
        val json = dataStore.data.first()[USER_DATA] ?: return null
        return try {
            Json.decodeFromString<User>(json)
        } catch (e: Exception) {
            null
        }
    }
    
    val userFlow: Flow<User?> = dataStore.data.map { prefs ->
        prefs[USER_DATA]?.let { json ->
            try {
                Json.decodeFromString<User>(json)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun setSuppressedUpdateVersion(version: String) {
        dataStore.edit { it[SUPPRESSED_UPDATE_VERSION] = version }
    }
    
    suspend fun getSuppressedUpdateVersion(): String? {
        return dataStore.data.first()[SUPPRESSED_UPDATE_VERSION]
    }
}
