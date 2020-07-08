package com.example.techtask

interface Events {
    class ArticleClickEvent(val url: String) : Events
    class LoadNewsAfterError : Events
}