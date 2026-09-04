package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** plan §12-13 endpoint #7. */

@Serializable
data class ObservationRequestDto(
    val id: String,
    val sectionIndex: Int,
    val category: String,
    val severity: String,
    val notes: String,
    val linkedEvidenceId: String? = null,
    val gpsPointId: String? = null,
    val recordedAt: Long
)

@Serializable
data class ObservationResponseDto(
    val id: String,
    val syncState: String
)
