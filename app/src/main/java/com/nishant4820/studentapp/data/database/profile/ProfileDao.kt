package com.nishant4820.studentapp.data.database.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profileEntity: ProfileEntity)

    @Query("SELECT * FROM profile_table ORDER BY id ASC")
    fun readProfile(): Flow<List<ProfileEntity>>
}