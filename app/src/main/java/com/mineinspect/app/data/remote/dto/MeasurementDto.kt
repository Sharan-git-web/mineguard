package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** plan §12-13 endpoint #8. */

@Serializable
data class MeasurementRequestDto(
    val id: String,
    val sectionIndex: Int,
    val metricType: String,
    val value: Double,
    val unit: String,
    val recordedAt: Long
)

@Serializable
data class MeasurementResponseDto(
    val id: String,
    val thresholdStatus: String? = null
)
