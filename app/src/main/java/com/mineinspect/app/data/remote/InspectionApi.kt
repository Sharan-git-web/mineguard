package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.CreateInspectionRequestDto
import com.mineinspect.app.data.remote.dto.InspectionResponseDto
import com.mineinspect.app.data.remote.dto.SubmitRequestDto
import com.mineinspect.app.data.remote.dto.SubmitResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/** plan §12-13, endpoints #4 and #13. */
interface InspectionApi {
    @POST("inspections")
    suspend fun createInspection(@Body request: CreateInspectionRequestDto): InspectionResponseDto

    @POST("inspections/{inspectionId}/submit")
    suspend fun submit(
        @Path("inspectionId") inspectionId: String,
        @Body request: SubmitRequestDto
    ): SubmitResponseDto
}
