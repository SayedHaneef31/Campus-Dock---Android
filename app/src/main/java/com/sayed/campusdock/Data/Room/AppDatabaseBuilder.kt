package com.sayed.campusdock.Data.Room

import android.content.Context
import androidx.room.Room

object AppDatabaseBuilder {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "CampusDock_DB"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}