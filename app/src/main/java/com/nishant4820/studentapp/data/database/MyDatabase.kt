package com.nishant4820.studentapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nishant4820.studentapp.data.database.notices.NoticesDao
import com.nishant4820.studentapp.data.database.notices.NoticesEntity
import com.nishant4820.studentapp.data.database.profile.ProfileDao
import com.nishant4820.studentapp.data.database.profile.ProfileEntity
import com.nishant4820.studentapp.data.database.results.ResultsDao
import com.nishant4820.studentapp.data.database.results.ResultsEntity
import com.nishant4820.studentapp.data.database.settings.SettingsDao
import com.nishant4820.studentapp.data.database.settings.SettingsEntity

@Database(
    entities = [NoticesEntity::class, SettingsEntity::class, ResultsEntity::class, ProfileEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MyTypeConverter::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun noticesDao(): NoticesDao

    abstract fun settingsDao(): SettingsDao

    abstract fun resultsDao(): ResultsDao

    abstract fun profileDao(): ProfileDao

}