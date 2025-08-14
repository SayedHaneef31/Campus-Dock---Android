package com.sayed.campusdock.Data.Room

import androidx.room.Database
import androidx.room.RoomDatabase

//Step 3

@Database(entities = [CartItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
