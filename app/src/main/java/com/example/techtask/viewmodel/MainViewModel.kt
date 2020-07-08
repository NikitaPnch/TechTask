package com.example.techtask.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.techtask.db.repository.NewsRepository

class MainViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    val news = newsRepository.news
    val hasNext = newsRepository.hasNext
    var isLoading = MutableLiveData(false)

    override suspend fun listen(action: Actions) {
        super.listen(action)

        when (action) {
            is MainActions.GetEverything -> getEverything()
        }
    }

    // получает новости
    private suspend fun getEverything() {
        isLoading.value = true
        newsRepository.getEverything()
        isLoading.value = false
    }
}