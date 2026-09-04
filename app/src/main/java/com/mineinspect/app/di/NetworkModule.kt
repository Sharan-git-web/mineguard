package com.mineinspect.app.di

import com.mineinspect.app.BuildConfig
import com.mineinspect.app.data.remote.AuthApi
import com.mineinspect.app.data.remote.AuthInterceptor
import com.mineinspect.app.data.remote.EvidenceApi
import com.mineinspect.app.data.remote.GpsPointApi
import com.mineinspect.app.data.remote.InspectionApi
import com.mineinspect.app.data.remote.MeasurementApi
import com.mineinspect.app.data.remote.MineApi
import com.mineinspect.app.data.remote.ObservationApi
import com.mineinspect.app.data.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit
import javax.inject.Singleton

/** Retrofit/OkHttp client for the FastAPI backend (plan §12-13). The app never talks to
 *  Supabase directly except the one presigned-URL binary PUT (plan §19), which is wired
 *  separately in a later phase and does not go through this Retrofit instance. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .authenticator(tokenAuthenticator)
            .build()
    }

    /** Bare client (no auth header, no 401 authenticator) used only by [TokenAuthenticator]
     *  to call `/auth/refresh` itself — reusing the main authenticated client there would
     *  be circular (its own 401s would re-trigger this same authenticator). */
    @Provides
    @Singleton
    @RefreshOkHttpClient
    fun provideRefreshOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideMineApi(retrofit: Retrofit): MineApi = retrofit.create(MineApi::class.java)

    @Provides
    @Singleton
    fun provideInspectionApi(retrofit: Retrofit): InspectionApi = retrofit.create(InspectionApi::class.java)

    @Provides
    @Singleton
    fun provideGpsPointApi(retrofit: Retrofit): GpsPointApi = retrofit.create(GpsPointApi::class.java)

    @Provides
    @Singleton
    fun provideObservationApi(retrofit: Retrofit): ObservationApi = retrofit.create(ObservationApi::class.java)

    @Provides
    @Singleton
    fun provideMeasurementApi(retrofit: Retrofit): MeasurementApi = retrofit.create(MeasurementApi::class.java)

    @Provides
    @Singleton
    fun provideEvidenceApi(retrofit: Retrofit): EvidenceApi = retrofit.create(EvidenceApi::class.java)
}
