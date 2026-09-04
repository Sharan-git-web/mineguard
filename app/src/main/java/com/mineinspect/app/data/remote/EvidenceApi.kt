package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.ConfirmUploadRequestDto
import com.mineinspect.app.data.remote.dto.ConfirmUploadResponseDto
import com.mineinspect.app.data.remote.dto.EvidenceRegisterRequestDto
import com.mineinspect.app.data.remote.dto.EvidenceRegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface EvidenceApi {
    @POST("evidence")
    suspend fun register(@Body request: EvidenceRegisterRequestDto): EvidenceRegisterResponseDto

    @POST("evidence/{evidenceId}/confirm-upload")
    suspend fun confirmUpload(
        @Path("evidenceId") evidenceId: String,
        @Body request: ConfirmUploadRequestDto
    ): ConfirmUploadResponseDto
}
