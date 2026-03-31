package com.flixify.app.domain.repository

import com.flixify.app.domain.model.CatalogResponse
import com.flixify.app.domain.model.LiveChannel
import com.flixify.app.domain.model.Movie
import com.flixify.app.domain.model.PlaybackSource
import com.flixify.app.domain.model.Resource
import com.flixify.app.domain.model.Series

interface MediaRepository {
    suspend fun getMovies(page: Int = 1, search: String? = null): Resource<CatalogResponse<Movie>>
    suspend fun getSeries(page: Int = 1, search: String? = null): Resource<CatalogResponse<Series>>
    suspend fun getLiveChannels(page: Int = 1, search: String? = null): Resource<CatalogResponse<LiveChannel>>
    
    suspend fun getVodPlaybackUrl(contentType: String, contentId: String): Resource<PlaybackSource>
    suspend fun getLivePlaybackUrl(channelId: String): Resource<PlaybackSource>
    
    suspend fun searchMovies(query: String): Resource<CatalogResponse<Movie>>
    suspend fun searchSeries(query: String): Resource<CatalogResponse<Series>>
    suspend fun searchLiveChannels(query: String): Resource<CatalogResponse<LiveChannel>>
}
