package com.flixify.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogResponse<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 0,
    val total: Int = 0,
    val hasMore: Boolean = false
)
