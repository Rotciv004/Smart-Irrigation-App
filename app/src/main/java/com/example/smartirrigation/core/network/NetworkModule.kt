package com.example.smartirrigation.core.network

import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.remote.EspApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = false
        explicitNulls = false
    }

    fun createEspApiService(config: DeviceConnectionConfig): EspApiService {
        return createRetrofit(buildBaseUrl(config)).create(EspApiService::class.java)
    }

    fun buildBaseUrl(config: DeviceConnectionConfig): String {
        return "http://${config.host}:${config.port}/"
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(ApiConstants.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConstants.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(ApiConstants.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }
}

