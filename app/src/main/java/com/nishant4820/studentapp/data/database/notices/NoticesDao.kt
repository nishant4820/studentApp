package com.nishant4820.studentapp.data.database.notices

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoticesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(noticesEntity: NoticesEntity)

    @Query("SELECT * FROM notices_table ORDER BY id ASC")
    fun readNotices(): Flow<List<NoticesEntity>>
}