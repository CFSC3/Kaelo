package com.mobile.kaelo.di

import android.content.Context
import androidx.room.Room
import com.mobile.kaelo.data.local.AppDatabase
import com.mobile.kaelo.data.local.dao.HabitDao
import com.mobile.kaelo.data.local.dao.HabitRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kaelo_database"
        )

            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitRecordDao(database: AppDatabase): HabitRecordDao {
        return database.habitRecordDao()
    }
}