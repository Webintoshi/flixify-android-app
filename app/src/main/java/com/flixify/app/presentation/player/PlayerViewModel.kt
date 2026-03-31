package com.flixify.app.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flixify.app.domain.model.PlaybackSource
import com.flixify.app.domain.model.Resource
import com.flixify.app.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isLoading: Boolean = true,
    val playbackUrl: String? = null,
    val drmLicenseUrl: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val error: String? = null,
    val showControls: Boolean = true
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    
    fun loadContent(contentId: String, contentType: String, isLive: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = if (isLive) {
                mediaRepository.getLivePlaybackUrl(contentId)
            } else {
                mediaRepository.getVodPlaybackUrl(contentType, contentId)
            }
            
            when (result) {
                is Resource.Success -> {
                    val source = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            playbackUrl = source.streamUrl,
                            drmLicenseUrl = source.drmLicenseUrl
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun onPlayerReady() {
        _uiState.update { it.copy(isBuffering = false) }
    }
    
    fun onBuffering() {
        _uiState.update { it.copy(isBuffering = true) }
    }
    
    fun onPlaybackEnded() {
        _uiState.update { it.copy(isPlaying = false) }
    }
    
    fun onIsPlayingChanged(isPlaying: Boolean) {
        _uiState.update { it.copy(isPlaying = isPlaying) }
    }
    
    fun onError(errorMessage: String) {
        _uiState.update { it.copy(error = errorMessage, isBuffering = false) }
    }
    
    fun toggleControls() {
        _uiState.update { it.copy(showControls = !it.showControls) }
    }
    
    fun hideControls() {
        _uiState.update { it.copy(showControls = false) }
    }
    
    fun showControls() {
        _uiState.update { it.copy(showControls = true) }
    }
}
