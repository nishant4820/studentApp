package com.nishant4820.studentapp.data.database.results

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nishant4820.studentapp.data.models.StudentResultResponse
import com.nishant4820.studentapp.utils.Constants.RESULTS_TABLE

@Entity(tableName = RESULTS_TABLE)
data class ResultsEntity(
    var results: StudentResultResponse
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}