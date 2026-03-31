package com.flixify.app.presentation.livetv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flixify.app.domain.model.LiveChannel
import com.flixify.app.domain.model.Resource
import com.flixify.app.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveTvUiState(
    val isLoading: Boolean = true,
    val channels: List<LiveChannel> = emptyList(),
    val filteredChannels: List<LiveChannel> = emptyList(),
    val currentCategory: String? = null,
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class LiveTvViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LiveTvUiState())
    val uiState: StateFlow<LiveTvUiState> = _uiState.asStateFlow()
    
    init {
        loadChannels()
    }
    
    private fun loadChannels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = mediaRepository.getLiveChannels(page = 1)) {
                is Resource.Success -> {
                    val channels = result.data?.items ?: emptyList()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            channels = channels,
                            filteredChannels = channels
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    
    fun searchChannels(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }
    
    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(currentCategory = category) }
        applyFilters()
    }
    
    private fun applyFilters() {
        val currentState = _uiState.value
        var filtered = currentState.channels
        
        // Apply category filter
        currentState.currentCategory?.let { category ->
            filtered = filtered.filter { it.group == category }
        }
        
        // Apply search filter
        if (currentState.searchQuery.isNotBlank()) {
            filtered = filtered.filter { 
                it.name.contains(currentState.searchQuery, ignoreCase = true) ||
                it.group?.contains(currentState.searchQuery, ignoreCase = true) == true
            }
        }
        
        _uiState.update { it.copy(filteredChannels = filtered) }
    }
    
    fun refresh() {
        loadChannels()
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
