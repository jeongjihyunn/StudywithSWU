package com.example.sws

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate?{
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToToTimestamp(date: LocalDate?): Long?{
        return date?.toEpochDay()
    }
}