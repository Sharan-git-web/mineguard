package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.LoginRequestDto
import com.mineinspect.app.data.remote.dto.LoginResponseDto
import com.mineinspect.app.data.remote.dto.RefreshRequestDto
import com.mineinspect.app.data.remote.dto.RefreshResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * FastAPI auth endpoints (plan §12-13, #1-2). NOTE: how the backend bridges
 * Inspector ID + PIN to a Supabase-backed session is an explicitly unresolved
 * design decision (plan §11) — this contract is fixed regardless of that choice.
 */
interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequestDto): RefreshResponseDto
}
