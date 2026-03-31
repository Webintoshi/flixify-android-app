package com.flixify.app.data.repository

import com.flixify.app.data.remote.ApiService
import com.flixify.app.domain.model.CatalogResponse
import com.flixify.app.domain.model.LiveChannel
import com.flixify.app.domain.model.Movie
import com.flixify.app.domain.model.PlaybackSource
import com.flixify.app.domain.model.Resource
import com.flixify.app.domain.model.Series
import com.flixify.app.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MediaRepository {
    
    override suspend fun getMovies(page: Int, search: String?): Resource<CatalogResponse<Movie>> {
        return try {
            val response = apiService.getMovies(
                page = page,
                search = search?.takeIf { it.isNotBlank() }
            )
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Failed to load movies: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun getSeries(page: Int, search: String?): Resource<CatalogResponse<Series>> {
        return try {
            val response = apiService.getSeries(
                page = page,
                search = search?.takeIf { it.isNotBlank() }
            )
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Failed to load series: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun getLiveChannels(page: Int, search: String?): Resource<CatalogResponse<LiveChannel>> {
        return try {
            val response = apiService.getLiveChannels(
                page = page,
                search = search?.takeIf { it.isNotBlank() }
            )
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Failed to load live channels: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun getVodPlaybackUrl(contentType: String, contentId: String): Resource<PlaybackSource> {
        return try {
            val response = apiService.getVodPlaybackUrl(
                kind = contentType,
                id = contentId
            )
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Failed to get playback URL: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun getLivePlaybackUrl(channelId: String): Resource<PlaybackSource> {
        return try {
            val response = apiService.getLivePlaybackUrl(channelId)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Failed to get live stream URL: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun searchMovies(query: String): Resource<CatalogResponse<Movie>> {
        return getMovies(page = 1, search = query)
    }
    
    override suspend fun searchSeries(query: String): Resource<CatalogResponse<Series>> {
        return getSeries(page = 1, search = query)
    }
    
    override suspend fun searchLiveChannels(query: String): Resource<CatalogResponse<LiveChannel>> {
        return getLiveChannels(page = 1, search = query)
    }
}
