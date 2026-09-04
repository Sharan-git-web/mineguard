package com.mineinspect.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.dao.SectionDefDao
import com.mineinspect.app.data.local.entity.EvidenceEntity
import com.mineinspect.app.data.local.entity.GpsPointEntity
import com.mineinspect.app.data.local.entity.InspectionEntity
import com.mineinspect.app.data.local.entity.MeasurementEntity
import com.mineinspect.app.data.local.entity.MineCacheEntity
import com.mineinspect.app.data.local.entity.ObservationEntity
import com.mineinspect.app.data.local.entity.SectionDefEntity

/** Offline-first local database per INSPECTOR_APP_BACKEND_INTEGRATION_PLAN.md §5. */
@Database(
    entities = [
        MineCacheEntity::class,
        SectionDefEntity::class,
        InspectionEntity::class,
        EvidenceEntity::class,
        GpsPointEntity::class,
        ObservationEntity::class,
        MeasurementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mineDao(): MineDao
    abstract fun sectionDefDao(): SectionDefDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun gpsPointDao(): GpsPointDao
    abstract fun observationDao(): ObservationDao
    abstract fun measurementDao(): MeasurementDao

    companion object {
        const val DATABASE_NAME = "mineinspect.db"
    }
}
