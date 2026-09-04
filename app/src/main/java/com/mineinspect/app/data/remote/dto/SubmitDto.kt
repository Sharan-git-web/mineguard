package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** plan §12-13 endpoint #13. */

@Serializable
data class SubmitRequestDto(val finalGpsPointId: String? = null)

@Serializable
data class SubmitResponseDto(val status: String)
