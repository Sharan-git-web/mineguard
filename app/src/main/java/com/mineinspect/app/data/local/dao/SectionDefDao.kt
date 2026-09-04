package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.SectionDefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDefDao {
    @Upsert
    suspend fun upsertAll(sections: List<SectionDefEntity>)

    @Query("SELECT * FROM section_def WHERE mineId = :mineId ORDER BY sectionIndex")
    fun observeForMine(mineId: String): Flow<List<SectionDefEntity>>

    @Query("SELECT * FROM section_def WHERE mineId = :mineId ORDER BY sectionIndex")
    suspend fun getForMine(mineId: String): List<SectionDefEntity>
}
