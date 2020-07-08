package com.example.techtask.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.techtask.db.DBNews

@Dao
interface NewsDao {

    @Transaction
    fun updateNews(news: List<DBNews>) {
        deleteAllNews()
        insertNews(news)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: List<DBNews>)

    @Query("SELECT * FROM DBNews")
    fun getNews(): List<DBNews>

    @Query("SELECT * FROM DBNews")
    fun queryNews(): LiveData<List<DBNews>>

    @Query("DELETE FROM DBNews")
    fun deleteAllNews()
}