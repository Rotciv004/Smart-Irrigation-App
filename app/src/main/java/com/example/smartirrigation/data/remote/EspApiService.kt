package com.example.smartirrigation.data.remote

import com.example.smartirrigation.core.network.ApiConstants
import com.example.smartirrigation.data.remote.dto.EmptyCommandRequest
import com.example.smartirrigation.data.remote.dto.SetModeRequest
import com.example.smartirrigation.data.remote.dto.SetTargetHumidityRequest
import com.example.smartirrigation.data.remote.dto.StatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EspApiService {
    @GET(ApiConstants.STATUS_ENDPOINT)
    suspend fun getStatus(): StatusResponse

    @POST(ApiConstants.TARGET_HUMIDITY_ENDPOINT)
    suspend fun setTargetHumidity(@Body request: SetTargetHumidityRequest): StatusResponse

    @POST(ApiConstants.PUMP_START_ENDPOINT)
    suspend fun startPump(@Body request: EmptyCommandRequest = EmptyCommandRequest()): StatusResponse

    @POST(ApiConstants.PUMP_STOP_ENDPOINT)
    suspend fun stopPump(@Body request: EmptyCommandRequest = EmptyCommandRequest()): StatusResponse

    @POST(ApiConstants.MODE_ENDPOINT)
    suspend fun setMode(@Body request: SetModeRequest): StatusResponse
}

