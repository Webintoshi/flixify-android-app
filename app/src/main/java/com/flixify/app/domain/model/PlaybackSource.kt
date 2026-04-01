package com.flixify.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaybackSource(
    val streamUrl: String,
    val drmLicenseUrl: String? = null,
    val deliveryMode: String? = null,
    val audioTracks: List<AudioTrack>? = null,
    val subtitleTracks: List<SubtitleTrack>? = null
)

@Serializable
data class AudioTrack(
    val id: String,
    val label: String,
    val language: String? = null
)

@Serializable
data class SubtitleTrack(
    val id: String,
    val label: String,
    val language: String? = null,
    val url: String? = null
)
