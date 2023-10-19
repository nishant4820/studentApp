package com.nishant4820.studentapp.data.database.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nishant4820.studentapp.data.models.SettingsResponse
import com.nishant4820.studentapp.utils.Constants.SETTINGS_TABLE

@Entity(tableName = SETTINGS_TABLE)
data class SettingsEntity(
    var settings: SettingsResponse
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}