package com.nishant4820.studentapp.data

import com.nishant4820.studentapp.data.database.NoticesDao
import com.nishant4820.studentapp.data.database.NoticesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val noticesDao: NoticesDao
) {

    fun readNotices(): Flow<List<NoticesEntity>> {
        return noticesDao.readNotices()
    }

    suspend fun insertNotices(noticesEntity: NoticesEntity) {
        noticesDao.insertNotices(noticesEntity)
    }
}