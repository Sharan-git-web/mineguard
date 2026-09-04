package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** `POST /inspections` request/response shape (plan §12-13, endpoint #4). */

@Serializable
data class CreateInspectionRequestDto(
    val id: String,
    val mineId: String,
    val inspectorId: String,
    val startedAt: Long,
    val gpsGateResult: String? = null
)

@Serializable
data class InspectionResponseDto(
    val id: String,
    val status: String
)
