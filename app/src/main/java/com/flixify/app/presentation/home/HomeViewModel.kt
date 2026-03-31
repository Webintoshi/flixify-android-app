package com.flixify.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flixify.app.domain.model.LiveChannel
import com.flixify.app.domain.model.Movie
import com.flixify.app.domain.model.Resource
import com.flixify.app.domain.model.Series
import com.flixify.app.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val heroContent: Movie? = null,
    val trendingMovies: List<Movie> = emptyList(),
    val newReleases: List<Movie> = emptyList(),
    val continueWatching: List<Movie> = emptyList(),
    val popularSeries: List<Series> = emptyList(),
    val liveChannels: List<LiveChannel> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load movies
                when (val moviesResult = mediaRepository.getMovies(page = 1)) {
                    is Resource.Success -> {
                        val movies = moviesResult.data?.items ?: emptyList()
                        _uiState.update { state ->
                            state.copy(
                                heroContent = movies.firstOrNull(),
                                trendingMovies = movies.take(10),
                                newReleases = movies.drop(10).take(10)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = moviesResult.message) }
                    }
                    is Resource.Loading -> {}
                }
                
                // Load series
                when (val seriesResult = mediaRepository.getSeries(page = 1)) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                popularSeries = seriesResult.data?.items?.take(10) ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
                
                // Load live channels
                when (val liveResult = mediaRepository.getLiveChannels(page = 1)) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                liveChannels = liveResult.data?.items?.take(10) ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message) 
                }
            }
        }
    }
    
    fun refresh() {
        loadHomeData()
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
