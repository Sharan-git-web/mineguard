package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.MineCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MineDao {
    @Upsert
    suspend fun upsertAll(mines: List<MineCacheEntity>)

    @Query("SELECT * FROM mine_cache ORDER BY name")
    fun observeAll(): Flow<List<MineCacheEntity>>

    @Query("SELECT * FROM mine_cache WHERE mineId = :mineId")
    suspend fun getById(mineId: String): MineCacheEntity?

    @Query("DELETE FROM mine_cache")
    suspend fun clearAll()
}
