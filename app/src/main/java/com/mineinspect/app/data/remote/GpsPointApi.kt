package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.GpsPointsBatchRequestDto
import com.mineinspect.app.data.remote.dto.GpsPointsBatchResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface GpsPointApi {
    @POST("inspections/{inspectionId}/gps-points")
    suspend fun upsertBatch(
        @Path("inspectionId") inspectionId: String,
        @Body request: GpsPointsBatchRequestDto
    ): GpsPointsBatchResponseDto
}
