package com.example.moodnotes.db

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss")

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime): String {
        return date.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(date: String): LocalDateTime {
        return LocalDateTime.parse(date, formatter)
    }
}