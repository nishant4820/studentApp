package com.nishant4820.studentapp.di

import android.content.Context
import androidx.room.Room
import com.nishant4820.studentapp.data.database.MyDatabase
import com.nishant4820.studentapp.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        MyDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideNoticesDao(database: MyDatabase) = database.noticesDao()

    @Singleton
    @Provides
    fun provideSettingsDao(database: MyDatabase) = database.settingsDao()

    @Singleton
    @Provides
    fun provideResultsDao(database: MyDatabase) = database.resultsDao()

}