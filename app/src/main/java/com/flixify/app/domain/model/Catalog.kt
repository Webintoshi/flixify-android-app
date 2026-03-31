package com.flixify.app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Live TV Models
@Serializable
data class LiveChannel(
    val id: String,
    val title: String,
    @SerialName("groupTitle") val groupTitle: String? = null,
    @SerialName("logoUrl") val logoUrl: String? = null,
    @SerialName("streamUrl") val streamUrl: String? = null,
    @SerialName("playbackAllowed") val playbackAllowed: Boolean = true,
    val transport: String? = null,
    @SerialName("variantGroupKey") val variantGroupKey: String? = null,
    @SerialName("qualityRank") val qualityRank: Int = -1,
    @SerialName("healthStatus") val healthStatus: String? = null,
    @SerialName("isVerified") val isVerified: Boolean = false
)

@Serializable
data class LiveGroup(
    val title: String,
    val count: Int,
    val kind: String = "live"
)

@Serializable
data class LiveCatalogResponse(
    val items: List<LiveChannel>,
    val groups: List<LiveGroup>,
    val total: Int
)

// Movie Models
@Serializable
data class Movie(
    val id: String,
    val title: String,
    @SerialName("posterUrl") val posterUrl: String? = null,
    @SerialName("streamImageUrl") val streamImageUrl: String? = null,
    @SerialName("stream_icon") val streamIcon: String? = null,
    @SerialName("groupTitle") val groupTitle: String? = null,
    @SerialName("streamUrl") val streamUrl: String? = null,
    @SerialName("playbackAllowed") val playbackAllowed: Boolean = true
)

@Serializable
data class MovieGroup(
    val title: String,
    val count: Int,
    val kind: String
)

@Serializable
data class MovieCatalogResponse(
    val items: List<Movie>,
    val groups: List<MovieGroup>,
    val total: Int
)

// Series Models
@Serializable
data class Series(
    val id: String,
    val title: String,
    @SerialName("posterUrl") val posterUrl: String? = null,
    @SerialName("groupTitle") val groupTitle: String? = null,
    @SerialName("seasonCount") val seasonCount: Int = 0,
    @SerialName("episodeCount") val episodeCount: Int = 0,
    val seasons: List<Season> = emptyList(),
    @SerialName("featuredEpisode") val featuredEpisode: Episode? = null
)

@Serializable
data class Season(
    @SerialName("seasonNumber") val seasonNumber: Int,
    val title: String? = null,
    @SerialName("episodeCount") val episodeCount: Int = 0,
    val episodes: List<Episode> = emptyList()
)

@Serializable
data class Episode(
    val id: String,
    val title: String,
    @SerialName("seasonNumber") val seasonNumber: Int = 0,
    @SerialName("episodeNumber") val episodeNumber: Int = 0,
    @SerialName("streamUrl") val streamUrl: String? = null,
    @SerialName("playbackAllowed") val playbackAllowed: Boolean = true
)

@Serializable
data class SeriesCatalogResponse(
    val items: List<Series>
)

// Playback Models
@Serializable
data class PlaybackSource(
    val url: String,
    val transport: String,
    @SerialName("deliveryMode") val deliveryMode: String? = null,
    @SerialName("userAgent") val userAgent: String? = null,
    val headers: Map<String, String>? = null,
    val cookie: String? = null,
    @SerialName("diagnosticsSessionId") val diagnosticsSessionId: String? = null
)

@Serializable
data class VodPlaybackRequest(
    val kind: String, // "movie" or "episode"
    val itemId: String,
    @SerialName("audioTrackId") val audioTrackId: String? = null
)

@Serializable
data class VodPlaybackResponse(
    val url: String? = null,
    val transport: String? = null,
    @SerialName("deliveryMode") val deliveryMode: String? = null,
    @SerialName("audioTracks") val audioTracks: List<AudioTrack> = emptyList(),
    @SerialName("defaultAudioTrackId") val defaultAudioTrackId: String? = null,
    @SerialName("selectedAudioTrackId") val selectedAudioTrackId: String? = null,
    @SerialName("canPlay") val canPlay: Boolean = false,
    @SerialName("isVerified") val isVerified: Boolean = false,
    @SerialName("errorMessage") val errorMessage: String? = null,
    @SerialName("expiresAt") val expiresAt: String? = null
)

@Serializable
data class AudioTrack(
    val id: String,
    val language: String? = null,
    val title: String? = null,
    val channels: Int? = null,
    @SerialName("isDefault") val isDefault: Boolean = false
)
