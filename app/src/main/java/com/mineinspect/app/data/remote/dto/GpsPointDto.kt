package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** plan §12-13 endpoint #6 (batched). */

@Serializable
data class GpsPointDto(
    val id: String,
    val sectionIndex: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val source: String,
    val capturedAt: Long
)

@Serializable
data class GpsPointsBatchRequestDto(val points: List<GpsPointDto>)

@Serializable
data class GpsPointsBatchResponseDto(val accepted: List<String>)
