package com.mineinspect.app.data.remote

import com.mineinspect.app.BuildConfig
import com.mineinspect.app.data.remote.dto.RefreshRequestDto
import com.mineinspect.app.data.remote.dto.RefreshResponseDto
import com.mineinspect.app.data.security.TokenStore
import com.mineinspect.app.di.RefreshOkHttpClient
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements the 401 handling the plan promised (plan §11/§14: "silent refresh, then
 * forced logout if refresh also fails") but that never actually existed in code —
 * AuthInterceptor only ever attached whatever token was stored, and a 401 (stale/expired
 * access token, or the debug "Skip Login" token, which is never a real backend-issued JWT)
 * was previously indistinguishable from a network error to SyncMetadataWorker: it just
 * retried the same doomed request until it gave up and marked the row SYNC_FAILED. That is
 * the root cause of records silently never reaching the server.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    @RefreshOkHttpClient private val refreshClient: OkHttpClient,
    private val json: Json
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (path.endsWith("/auth/refresh") || path.endsWith("/auth/login")) return null
        if (responseCount(response) >= 2) return null

        synchronized(this) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentToken = tokenStore.getAccessToken()

            // A concurrent request already refreshed while this one waited for the lock.
            if (currentToken != null && currentToken != failedToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = tokenStore.getRefreshToken()
            val newAccessToken = if (refreshToken != null) requestNewAccessToken(refreshToken) else null

            if (newAccessToken == null) {
                tokenStore.clear()
                return null
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }

    private fun requestNewAccessToken(refreshToken: String): String? {
        return try {
            val requestJson = json.encodeToString(
                RefreshRequestDto.serializer(),
                RefreshRequestDto(refreshToken)
            )
            val request = Request.Builder()
                .url(BuildConfig.API_BASE_URL + "auth/refresh")
                .post(requestJson.toRequestBody("application/json".toMediaType()))
                .build()

            refreshClient.newCall(request).execute().use { httpResponse ->
                if (!httpResponse.isSuccessful) return null
                val body = httpResponse.body?.string() ?: return null
                val refreshed = json.decodeFromString(RefreshResponseDto.serializer(), body)
                val expiresAt = System.currentTimeMillis() + refreshed.expiresIn * 1000
                tokenStore.updateAccessToken(refreshed.accessToken, expiresAt)
                refreshed.accessToken
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
