package com.anroid.real_studyplanner

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudySession::class], version = 1)
abstract class StudyDatabase : RoomDatabase(){
    abstract fun studySessionDao(): StudySessionDao

    companion object{
        private var instance: StudyDatabase? =null

        fun getInstance(context: Context): StudyDatabase{
            return instance ?: Room.databaseBuilder(
                context.applicationContext,
                StudyDatabase::class.java,
                "study_timer_database"
            ).build().also { instance = it }
        }
    }
}