package com.nishant4820.studentapp.data

import com.nishant4820.studentapp.data.database.MyDatabase
import com.nishant4820.studentapp.data.database.notices.NoticesDao
import com.nishant4820.studentapp.data.database.notices.NoticesEntity
import com.nishant4820.studentapp.data.database.profile.ProfileDao
import com.nishant4820.studentapp.data.database.profile.ProfileEntity
import com.nishant4820.studentapp.data.database.results.ResultsDao
import com.nishant4820.studentapp.data.database.results.ResultsEntity
import com.nishant4820.studentapp.data.database.settings.SettingsDao
import com.nishant4820.studentapp.data.database.settings.SettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val myDatabase: MyDatabase,
    private val noticesDao: NoticesDao,
    private val settingsDao: SettingsDao,
    private val resultsDao: ResultsDao,
    private val profileDao: ProfileDao
) {

    fun clearAllTables() {
        myDatabase.clearAllTables()
    }

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

    fun readResults(): Flow<List<ResultsEntity>> {
        return resultsDao.readResults()
    }

    suspend fun insertResults(resultsEntity: ResultsEntity) {
        resultsDao.insertResults(resultsEntity)
    }

    fun readProfile(): Flow<List<ProfileEntity>> {
        return profileDao.readProfile()
    }

    suspend fun insertProfile(profileEntity: ProfileEntity) {
        profileDao.insertProfile(profileEntity)
    }
}