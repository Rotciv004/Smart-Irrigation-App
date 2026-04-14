package com.example.smartirrigation.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.smartirrigation.core.network.ApiConstants
import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.settings.SettingsDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {
    @Test
    fun default_values_areReturned() = runTest {
        val repository = createRepository()

        assertEquals(DeviceConnectionConfig(ApiConstants.DEFAULT_HOST, ApiConstants.DEFAULT_PORT), repository.observeConnectionConfig().first())
        assertEquals(ApiConstants.DEFAULT_THEME_MODE, repository.observeThemeMode().first())
        assertEquals(ApiConstants.DEFAULT_POLLING_INTERVAL_MS, repository.observePollingInterval().first())
    }

    @Test
    fun saved_values_areRestoredCorrectly() = runTest {
        val repository = createRepository()
        repository.saveConnectionConfig(DeviceConnectionConfig("192.168.1.55", 8080))
        repository.saveThemeMode(AppThemeMode.DARK)
        repository.savePollingInterval(4000)

        assertEquals(DeviceConnectionConfig("192.168.1.55", 8080), repository.observeConnectionConfig().first())
        assertEquals(AppThemeMode.DARK, repository.observeThemeMode().first())
        assertEquals(4000, repository.observePollingInterval().first())
    }

    private fun createRepository(): SettingsRepositoryImpl {
        val tempFile = File.createTempFile("settings-test", ".preferences_pb")
        tempFile.deleteOnExit()
        val dataStore = PreferenceDataStoreFactory.create(produceFile = { tempFile })
        return SettingsRepositoryImpl(SettingsDataStore(dataStore))
    }
}

