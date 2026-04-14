package com.example.smartirrigation.data.repository

import com.example.smartirrigation.core.network.ApiConstants
import com.example.smartirrigation.core.network.NetworkModule
import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.IrrigationMode
import com.example.smartirrigation.data.remote.dto.EmptyCommandRequest
import com.example.smartirrigation.data.remote.dto.SetModeRequest
import com.example.smartirrigation.data.remote.dto.SetTargetHumidityRequest
import com.example.smartirrigation.data.remote.dto.StatusResponse
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

open class DeviceRepositoryImpl(
    private val settingsRepository: SettingsRepository,
    private val networkModule: NetworkModule,
) : DeviceRepository {

    override suspend fun getStatus(): NetworkResult<DeviceStatus> =
        execute { service -> service.getStatus() }

    override suspend fun setTargetHumidity(value: Int): NetworkResult<DeviceStatus> {
        if (value !in 0..100) {
            return NetworkResult.UnknownError("Target humidity must be between 0 and 100.")
        }
        return execute { service ->
            service.setTargetHumidity(SetTargetHumidityRequest(targetHumidity = value))
        }
    }

    override suspend fun startPump(): NetworkResult<DeviceStatus> =
        execute { service -> service.startPump(EmptyCommandRequest()) }

    override suspend fun stopPump(): NetworkResult<DeviceStatus> =
        execute { service -> service.stopPump(EmptyCommandRequest()) }

    override suspend fun setMode(mode: IrrigationMode): NetworkResult<DeviceStatus> =
        execute { service ->
            service.setMode(SetModeRequest(mode = mode.name.lowercase()))
        }

    private suspend fun execute(
        block: suspend (com.example.smartirrigation.data.remote.EspApiService) -> StatusResponse,
    ): NetworkResult<DeviceStatus> {
        val config = settingsRepository.observeConnectionConfig().first()
        if (config.host.isBlank()) {
            return NetworkResult.NetworkError("No saved device IP address.")
        }
        if (config.port !in 1..65535) {
            return NetworkResult.NetworkError("Saved port is invalid.")
        }

        return executeWithConfig(config, block)
    }

    open suspend fun testConnection(config: DeviceConnectionConfig): NetworkResult<DeviceStatus> {
        if (config.host.isBlank()) {
            return NetworkResult.NetworkError("Device IP address is required.")
        }
        if (config.port !in 1..65535) {
            return NetworkResult.NetworkError("Port must be between 1 and 65535.")
        }
        return executeWithConfig(config) { service -> service.getStatus() }
    }

    private suspend fun executeWithConfig(
        config: DeviceConnectionConfig,
        block: suspend (com.example.smartirrigation.data.remote.EspApiService) -> StatusResponse,
    ): NetworkResult<DeviceStatus> {
        return try {
            val service = networkModule.createEspApiService(config)
            NetworkResult.Success(block(service).toDeviceStatus())
        } catch (exception: HttpException) {
            NetworkResult.HttpError(
                code = exception.code(),
                message = exception.message().orEmpty().ifBlank { "HTTP error ${exception.code()}" },
            )
        } catch (exception: IOException) {
            NetworkResult.NetworkError(exception.message ?: "Could not reach the ESP32 device.")
        } catch (exception: IllegalArgumentException) {
            NetworkResult.UnknownError(exception.message ?: "Invalid response from device.", exception)
        } catch (exception: Exception) {
            NetworkResult.UnknownError(exception.message ?: "Unexpected error.", exception)
        }
    }

    private fun StatusResponse.toDeviceStatus(): DeviceStatus {
        val normalizedMode = when (mode.lowercase()) {
            "auto" -> IrrigationMode.AUTO
            "manual" -> IrrigationMode.MANUAL
            else -> throw IllegalArgumentException("Unsupported irrigation mode: $mode")
        }
        return DeviceStatus(
            humidity = humidity.coerceIn(0, 100),
            targetHumidity = targetHumidity.coerceIn(0, 100),
            pumpOn = pumpOn,
            mode = normalizedMode,
            deviceName = deviceName,
        )
    }
}


