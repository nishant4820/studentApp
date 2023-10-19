package com.nishant4820.studentapp.data.database.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settingsEntity: SettingsEntity)

    @Query("SELECT * FROM settings_table ORDER BY id ASC")
    fun readSettings(): Flow<List<SettingsEntity>>

}