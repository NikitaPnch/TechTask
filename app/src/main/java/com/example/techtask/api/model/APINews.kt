package com.example.techtask.api.model

import com.example.techtask.db.DBNews

data class APINews(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
) {
    data class Article(
        val source: Source,
        val author: String,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String?,
        val publishedAt: String,
        val content: String
    ) {
        data class Source(
            val id: String,
            val name: String
        )
    }
}

// приводит модель api к модели базы данных
fun List<APINews.Article>.asDatabaseModel(): List<DBNews> {
    return map {
        DBNews(
            url = it.url,
            source = DBNews.Source(
                id = it.source.id,
                name = it.source.name
            ),
            author = it.author,
            content = it.content,
            title = it.title,
            description = it.description,
            urlToImage = it.urlToImage,
            publishedAt = it.publishedAt
        )
    }
}