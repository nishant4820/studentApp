package com.nishant4820.studentapp.data.database.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nishant4820.studentapp.data.models.StudentProfileResponse
import com.nishant4820.studentapp.utils.Constants.PROFILE_TABLE

@Entity(tableName = PROFILE_TABLE)
data class ProfileEntity(
    var profile: StudentProfileResponse
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}