package com.mineinspect.app.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Small, single-row, security-sensitive session store. Deliberately EncryptedSharedPreferences
 * rather than DataStore — see plan §11 for why that's the right call at this scale.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        expiresAtEpochMillis: Long,
        inspectorId: String,
        inspectorName: String
    ) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putLong(KEY_EXPIRES_AT, expiresAtEpochMillis)
            .putString(KEY_INSPECTOR_ID, inspectorId)
            .putString(KEY_INSPECTOR_NAME, inspectorName)
            .apply()
    }

    fun updateAccessToken(accessToken: String, expiresAtEpochMillis: Long) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putLong(KEY_EXPIRES_AT, expiresAtEpochMillis)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getExpiresAt(): Long = prefs.getLong(KEY_EXPIRES_AT, 0L)
    fun getInspectorId(): String? = prefs.getString(KEY_INSPECTOR_ID, null)
    fun getInspectorName(): String? = prefs.getString(KEY_INSPECTOR_NAME, null)
    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "mineinspect_secure_prefs"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_INSPECTOR_ID = "inspector_id"
        const val KEY_INSPECTOR_NAME = "inspector_name"
    }
}
