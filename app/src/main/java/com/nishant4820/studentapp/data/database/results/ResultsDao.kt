package com.nishant4820.studentapp.data.database.results

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(resultsEntity: ResultsEntity)

    @Query("SELECT * FROM results_table ORDER BY id ASC")
    fun readResults(): Flow<List<ResultsEntity>>
}