package com.mineinspect.app.data.remote.dto

import kotlinx.serialization.Serializable

/** Request/response bodies for `POST /auth/login` and `POST /auth/refresh` (plan §12-13, endpoints #1-2). */

@Serializable
data class LoginRequestDto(
    val inspectorId: String,
    val pin: String
)

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val inspector: InspectorDto
)

@Serializable
data class InspectorDto(
    val id: String,
    val name: String
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String
)

@Serializable
data class RefreshResponseDto(
    val accessToken: String,
    val expiresIn: Long
)
