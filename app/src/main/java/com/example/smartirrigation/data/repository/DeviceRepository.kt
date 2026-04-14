package com.example.smartirrigation.data.repository

import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.IrrigationMode

interface DeviceRepository {
    suspend fun getStatus(): NetworkResult<DeviceStatus>
    suspend fun setTargetHumidity(value: Int): NetworkResult<DeviceStatus>
    suspend fun startPump(): NetworkResult<DeviceStatus>
    suspend fun stopPump(): NetworkResult<DeviceStatus>
    suspend fun setMode(mode: IrrigationMode): NetworkResult<DeviceStatus>
}

