package com.example.techtask.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class DBNews(

    @PrimaryKey
    var url: String = "",
    @Embedded
    var source: Source = Source(
        "",
        ""
    ),
    var author: String? = "",
    var title: String? = "",
    var description: String? = "",
    var urlToImage: String? = "",
    var publishedAt: String = "",
    var content: String? = ""
) {
    data class Source(
        var id: String?,
        var name: String?
    )
}