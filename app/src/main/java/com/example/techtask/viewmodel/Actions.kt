package com.example.techtask.viewmodel

interface Actions {
    class Error(val error: Throwable) : Actions
}