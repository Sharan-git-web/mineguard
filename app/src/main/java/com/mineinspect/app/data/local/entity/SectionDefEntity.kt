package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Read-only cache of a Mine's section definitions (plan §5). Section *progress* is
 * deliberately not stored here — it is derived at query time via COUNT(*) over
 * EvidenceEntity, which is what fixes the double-increment bug documented in plan §22.
 */
@Entity(
    tableName = "section_def",
    indices = [Index("mineId")]
)
data class SectionDefEntity(
    @PrimaryKey val id: String, // "{mineId}:{sectionIndex}"
    val mineId: String,
    val sectionIndex: Int,
    val label: String,
    val description: String,
    val evidenceQuota: Int
)
