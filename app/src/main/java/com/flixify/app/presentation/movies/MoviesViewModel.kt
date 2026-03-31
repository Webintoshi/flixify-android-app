package com.flixify.app.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flixify.app.domain.model.Movie
import com.flixify.app.domain.model.Resource
import com.flixify.app.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoviesUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()
    
    init {
        loadMovies()
    }
    
    private fun loadMovies(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = mediaRepository.getMovies(page = page)) {
                is Resource.Success -> {
                    val newMovies = result.data?.items ?: emptyList()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            movies = if (page == 1) newMovies else state.movies + newMovies,
                            currentPage = page,
                            hasMorePages = newMovies.isNotEmpty()
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
    
    fun searchMovies(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isBlank()) {
            loadMovies(1)
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = mediaRepository.searchMovies(query)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            movies = result.data?.items ?: emptyList(),
                            hasMorePages = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun loadMore() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return
        loadMovies(_uiState.value.currentPage + 1)
    }
    
    fun refresh() {
        loadMovies(1)
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
