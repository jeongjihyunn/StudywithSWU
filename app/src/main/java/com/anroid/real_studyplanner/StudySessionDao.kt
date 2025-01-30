package com.anroid.real_studyplanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudySessionDao {

    @Query("SELECT SUM(duration) FROM study_sessions WHERE date = :selectedDate")
    fun getStudyTimeByDate(selectedDate: Long): Long

    @Query("SELECT startTime FROM study_sessions WHERE date = :selectedDate ORDER BY startTime DESC LIMIT 1")
    fun getLastStartTimeByDate(selectedDate: Long): Long?

    @Query("SELECT endTime FROM study_sessions WHERE date = :selectedDate ORDER BY endTime DESC LIMIT 1")
    fun getLastEndTimeByDate(selectedDate: Long): Long?

    @Insert
    fun insertStudySession(session: StudySession)
}
