package com.example.techtask.db.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.techtask.Constants
import com.example.techtask.api.model.APINews
import com.example.techtask.api.model.asDatabaseModel
import com.example.techtask.api.requests.Everything
import com.example.techtask.db.DBNews
import com.example.techtask.db.dao.NewsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.withContext

class NewsRepository(private val api: Everything, private val newsDao: NewsDao) {

    val news: LiveData<List<DBNews>> = newsDao.queryNews()
    private var currentPage = 1
    private var totalPages = 0
    var hasNext = MutableLiveData<Boolean>()

    // перезагрузить список новостей
    private suspend fun updateNews(articles: List<APINews.Article>) {
        newsDao.updateNews(articles.asDatabaseModel())
    }

    // получает свежие новости из текущей страны
    suspend fun getEverything() = withContext(Dispatchers.IO) {
        api.searchEverything(currentPage).await().let {
            getTotalPages(it.totalResults)
            updatePage()
            updateNews(it.articles)
        }
    }

    private fun getTotalPages(totalResults: Int) {
        if (totalPages == 0) {
            totalPages = totalResults / Constants.PAGE_SIZE
        }
    }

    private suspend fun updatePage() = withContext(Dispatchers.Main) {
        if (currentPage < totalPages) {
            hasNext.value = true
            currentPage++
        } else {
            hasNext.value = false
        }
    }
}