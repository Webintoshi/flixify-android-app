package com.flixify.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flixify.app.domain.model.AuthResult
import com.flixify.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

data class LoginUiState(
    val code: String = "",
    val showCode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val progress: Float = 0f // 0-16 characters
) {
    val isValid: Boolean get() = code.length == 16
    val formattedCode: String get() = code.chunked(4).joinToString(" ")
}

data class RegisterUiState(
    val isGenerating: Boolean = false,
    val generatedCode: String = "",
    val revealedCount: Int = 0,
    val isRevealing: Boolean = false,
    val isAcknowledged: Boolean = false,
    val error: String? = null
) {
    val isRevealed: Boolean get() = revealedCount >= generatedCode.length
    val progress: Float get() = if (generatedCode.isEmpty()) 0f else revealedCount.toFloat() / generatedCode.length
    val scrambledCode: String get() {
        if (generatedCode.isEmpty()) return "****************"
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return buildString {
            for (i in 0 until 16) {
                when {
                    i < revealedCount && i < generatedCode.length -> append(generatedCode[i])
                    i < generatedCode.length -> append(alphabet[(revealedCount * 11 + i * 7) % alphabet.length])
                    else -> append('*')
                }
            }
        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()
    
    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true) }
            
            authRepository.isLoggedIn.collect { isLoggedIn ->
                _authState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = isLoggedIn
                    )
                }
            }
        }
    }
    
    fun onCodeChanged(newCode: String) {
        // Only allow alphanumeric, auto-uppercase
        val sanitized = newCode.uppercase().filter { it.isLetterOrDigit() }.take(16)
        _loginState.update { 
            it.copy(
                code = sanitized,
                error = null,
                progress = sanitized.length / 16f
            )
        }
    }
    
    fun toggleShowCode() {
        _loginState.update { it.copy(showCode = !it.showCode) }
    }
    
    fun login() {
        val currentCode = _loginState.value.code
        if (currentCode.length != 16) return
        
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.login(currentCode)) {
                is AuthResult.Success -> {
                    _loginState.update { it.copy(isLoading = false) }
                    _authState.update { it.copy(isAuthenticated = true) }
                }
                is AuthResult.Error -> {
                    _loginState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.update { it.copy(isAuthenticated = false) }
            _loginState.update { LoginUiState() }
        }
    }
    
    fun generateCode() {
        viewModelScope.launch {
            _registerState.update { it.copy(isGenerating = true, error = null) }
            
            when (val result = authRepository.register()) {
                is AuthResult.Success -> {
                    val code = result.data.kryptoniteCode ?: ""
                    _registerState.update { 
                        it.copy(
                            isGenerating = false,
                            generatedCode = code,
                            revealedCount = 0,
                            isRevealing = true
                        )
                    }
                    startRevealAnimation(code)
                }
                is AuthResult.Error -> {
                    _registerState.update { 
                        it.copy(
                            isGenerating = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun startRevealAnimation(code: String) {
        viewModelScope.launch {
            // Warmup delay
            delay(800)
            
            // Reveal each character with increasing delay
            for (i in code.indices) {
                val delayMs = when {
                    i < 4 -> 138L
                    i < 8 -> 154L
                    i < 12 -> 170L
                    else -> 184L
                }
                delay(delayMs)
                _registerState.update { state ->
                    state.copy(revealedCount = i + 1)
                }
            }
            
            _registerState.update { it.copy(isRevealing = false) }
        }
    }
    
    fun toggleAcknowledged() {
        _registerState.update { it.copy(isAcknowledged = !it.isAcknowledged) }
    }
    
    fun copyCode(): String {
        return _registerState.value.generatedCode
    }
    
    fun clearError() {
        _loginState.update { it.copy(error = null) }
        _registerState.update { it.copy(error = null) }
    }
}
