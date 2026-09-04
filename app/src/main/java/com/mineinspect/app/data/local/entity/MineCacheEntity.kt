package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Read-only cache of a Mine, refreshed from `GET /mines` (plan §5, §12-13 #3).
 * Not a synced entity — the server is always the source of truth, never mutated locally.
 */
@Entity(tableName = "mine_cache")
data class MineCacheEntity(
    @PrimaryKey val mineId: String,
    val name: String,
    val permitNumber: String,
    val hazardIndex: Double,
    val evidenceQuota: Int,
    val sectionCount: Int,
    val lastBriefingText: String?,
    val cachedAt: Long
)
