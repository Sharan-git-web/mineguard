package com.mineinspect.app.data.remote

import com.mineinspect.app.data.security.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Attaches the stored bearer token to every request (plan §12-13: "JWT" auth on every endpoint except login). */
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenStore.getAccessToken()
        val request = if (token != null) {
            original.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
