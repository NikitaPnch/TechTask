package com.example.techtask.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.techtask.db.dao.NewsDao

@Database(
    entities = [DBNews::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
}