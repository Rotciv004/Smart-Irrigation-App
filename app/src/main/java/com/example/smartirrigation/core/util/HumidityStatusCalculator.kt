package com.example.smartirrigation.core.util

import com.example.smartirrigation.data.model.HumidityStatus

object HumidityStatusCalculator {
    fun calculate(humidity: Int, targetHumidity: Int): HumidityStatus {
        return when {
            humidity < targetHumidity - 3 -> HumidityStatus.DRY
            humidity > targetHumidity + 3 -> HumidityStatus.WET
            else -> HumidityStatus.GOOD
        }
    }
}

