package com.nishant4820.studentapp.data

import com.nishant4820.studentapp.data.database.notices.NoticesDao
import com.nishant4820.studentapp.data.database.notices.NoticesEntity
import com.nishant4820.studentapp.data.database.settings.SettingsDao
import com.nishant4820.studentapp.data.database.settings.SettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val noticesDao: NoticesDao,
    private val settingsDao: SettingsDao
) {

    fun readNotices(): Flow<List<NoticesEntity>> {
        return noticesDao.readNotices()
    }

    suspend fun insertNotices(noticesEntity: NoticesEntity) {
        noticesDao.insertNotices(noticesEntity)
    }

    fun readSettings(): Flow<List<SettingsEntity>> {
        return settingsDao.readSettings()
    }

    suspend fun insertSettings(settingsEntity: SettingsEntity) {
        settingsDao.insertSettings(settingsEntity)
    }
}