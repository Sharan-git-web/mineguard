package com.mineinspect.app.data.repository

import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.dao.SectionDefDao
import com.mineinspect.app.data.local.entity.MineCacheEntity
import com.mineinspect.app.data.local.entity.SectionDefEntity
import com.mineinspect.app.data.remote.MineApi
import com.mineinspect.app.data.remote.dto.MineDto
import com.mineinspect.app.data.remote.dto.SectionDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mines are a read-only cache (plan §5) — always refreshed from the server, never
 * mutated locally. `observeMines` serves the last-cached list instantly (e.g. offline
 * on Home); `refreshMines` is what actually talks to the network.
 */
@Singleton
class MineRepository @Inject constructor(
    private val mineApi: MineApi,
    private val mineDao: MineDao,
    private val sectionDefDao: SectionDefDao
) {
    fun observeMines(): Flow<List<MineCacheEntity>> = mineDao.observeAll()

    suspend fun refreshMines(inspectorId: String): Result<Unit> {
        return try {
            val mines = mineApi.getMines(inspectorId)
            val now = System.currentTimeMillis()
            mineDao.upsertAll(mines.map { it.toEntity(now) })
            sectionDefDao.upsertAll(mines.flatMap { mine -> mine.sections.map { it.toEntity(mine.id) } })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun MineDto.toEntity(cachedAt: Long) = MineCacheEntity(
    mineId = id,
    name = name,
    permitNumber = permitNumber,
    hazardIndex = hazardIndex,
    evidenceQuota = evidenceQuota,
    sectionCount = sectionCount,
    lastBriefingText = lastBriefingText,
    cachedAt = cachedAt
)

private fun SectionDto.toEntity(mineId: String) = SectionDefEntity(
    id = "$mineId:$index",
    mineId = mineId,
    sectionIndex = index,
    label = label,
    description = description,
    evidenceQuota = evidenceQuota
)
