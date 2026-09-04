package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** `GET /mines?assignedTo={inspectorId}` response shape (plan §12-13, endpoint #3). */

@Serializable
data class MineDto(
    val id: String,
    val name: String,
    val permitNumber: String,
    val hazardIndex: Double,
    val evidenceQuota: Int,
    val sectionCount: Int,
    val sections: List<SectionDto> = emptyList(),
    val lastBriefingText: String? = null
)

@Serializable
data class SectionDto(
    val index: Int,
    val label: String,
    val description: String,
    val evidenceQuota: Int
)
