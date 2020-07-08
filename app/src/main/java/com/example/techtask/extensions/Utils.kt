package com.example.techtask.extensions

import android.text.format.DateFormat.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


// вытаскивает timestamp из строки с датой
fun getTimestampFromString(dateStr: String): Long {
    val newString = dateStr.replace("T", " ").replace("Z", "")
    val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val date: Date = formatter.parse(newString) as Date
    return date.time
}

// получает дату из timestamp
fun getDate(timestamp: Long): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = timestamp
    return format("dd.MM.yyyy", calendar).toString()
}