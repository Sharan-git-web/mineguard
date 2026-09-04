package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.ObservationRequestDto
import com.mineinspect.app.data.remote.dto.ObservationResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ObservationApi {
    @POST("inspections/{inspectionId}/observations")
    suspend fun upsert(
        @Path("inspectionId") inspectionId: String,
        @Body request: ObservationRequestDto
    ): ObservationResponseDto
}
