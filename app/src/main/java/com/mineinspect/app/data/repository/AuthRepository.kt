package com.mineinspect.app.data.repository

import com.mineinspect.app.data.remote.AuthApi
import com.mineinspect.app.data.remote.dto.LoginRequestDto
import com.mineinspect.app.data.security.TokenStore
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val message: String) : AuthResult
}

/**
 * Real login flow, replacing LoginScreen's hardcoded unconditional sign-in
 * (plan §11, §22 item 9). Login requires connectivity — no offline/fabricated
 * session is ever created (plan §12-13, endpoint #1 offline behavior).
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(inspectorId: String, pin: String): AuthResult {
        return try {
            val response = authApi.login(LoginRequestDto(inspectorId = inspectorId, pin = pin))
            val expiresAt = System.currentTimeMillis() + response.expiresIn * 1000
            tokenStore.saveSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresAtEpochMillis = expiresAt,
                inspectorId = response.inspector.id,
                inspectorName = response.inspector.name
            )
            AuthResult.Success
        } catch (e: IOException) {
            AuthResult.Error("No connection to server. Check network and try again.")
        } catch (e: HttpException) {
            val message = if (e.code() == 401) {
                "Invalid Inspector ID or PIN."
            } else {
                "Server error (${e.code()}). Try again."
            }
            AuthResult.Error(message)
        } catch (e: Exception) {
            AuthResult.Error("Sign-in failed: ${e.message ?: "unknown error"}")
        }
    }

    fun isLoggedIn(): Boolean = tokenStore.isLoggedIn()

    fun logout() {
        tokenStore.clear()
    }
}
