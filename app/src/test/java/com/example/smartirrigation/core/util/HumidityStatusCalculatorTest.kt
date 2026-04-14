package com.example.smartirrigation.core.util

import com.example.smartirrigation.data.model.HumidityStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class HumidityStatusCalculatorTest {
    @Test
    fun dry_cases_areCalculated() {
        assertEquals(HumidityStatus.DRY, HumidityStatusCalculator.calculate(humidity = 40, targetHumidity = 50))
    }

    @Test
    fun good_cases_areCalculated() {
        assertEquals(HumidityStatus.GOOD, HumidityStatusCalculator.calculate(humidity = 47, targetHumidity = 50))
        assertEquals(HumidityStatus.GOOD, HumidityStatusCalculator.calculate(humidity = 53, targetHumidity = 50))
    }

    @Test
    fun wet_cases_areCalculated() {
        assertEquals(HumidityStatus.WET, HumidityStatusCalculator.calculate(humidity = 60, targetHumidity = 50))
    }
}

