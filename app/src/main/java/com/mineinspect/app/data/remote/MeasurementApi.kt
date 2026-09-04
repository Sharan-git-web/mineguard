package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.MeasurementRequestDto
import com.mineinspect.app.data.remote.dto.MeasurementResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MeasurementApi {
    @POST("inspections/{inspectionId}/measurements")
    suspend fun upsert(
        @Path("inspectionId") inspectionId: String,
        @Body request: MeasurementRequestDto
    ): MeasurementResponseDto
}
