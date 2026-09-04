package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** plan §12-13 endpoints #9-11. */

@Serializable
data class EvidenceRegisterRequestDto(
    val id: String,
    val inspectionId: String,
    val sectionIndex: Int,
    val capturedAt: Long,
    val gpsPointId: String? = null,
    val fileHash: String? = null
)

@Serializable
data class EvidenceRegisterResponseDto(
    val id: String,
    val uploadUrl: String
)

@Serializable
data class ConfirmUploadRequestDto(val objectPath: String)

@Serializable
data class ConfirmUploadResponseDto(
    val id: String,
    val uploadState: String,
    val syncState: String
)
