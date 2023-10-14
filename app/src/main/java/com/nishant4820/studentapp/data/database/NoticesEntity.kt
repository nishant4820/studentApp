package com.nishant4820.studentapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.utils.Constants.NOTICES_TABLE

@Entity(tableName = NOTICES_TABLE)
data class NoticesEntity(
    var notices: NoticeResponse
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}