package com.mineinspect.app.di

import android.content.Context
import androidx.room.Room
import com.mineinspect.app.data.local.AppDatabase
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.dao.SectionDefDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideMineDao(db: AppDatabase): MineDao = db.mineDao()

    @Provides
    fun provideSectionDefDao(db: AppDatabase): SectionDefDao = db.sectionDefDao()

    @Provides
    fun provideInspectionDao(db: AppDatabase): InspectionDao = db.inspectionDao()

    @Provides
    fun provideEvidenceDao(db: AppDatabase): EvidenceDao = db.evidenceDao()

    @Provides
    fun provideGpsPointDao(db: AppDatabase): GpsPointDao = db.gpsPointDao()

    @Provides
    fun provideObservationDao(db: AppDatabase): ObservationDao = db.observationDao()

    @Provides
    fun provideMeasurementDao(db: AppDatabase): MeasurementDao = db.measurementDao()
}
