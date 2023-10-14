package com.nishant4820.studentapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [NoticesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NoticesTypeConverter::class)
abstract class NoticesDatabase: RoomDatabase() {

    abstract fun noticesDao(): NoticesDao

}