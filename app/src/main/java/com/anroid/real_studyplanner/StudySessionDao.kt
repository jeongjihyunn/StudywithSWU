package com.anroid.real_studyplanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate

@Dao
interface StudySessionDao {
    @Query("SELECT SUM(duration) FROM study_sessions WHERE date = :selectedDate")
    fun getStudyTimeByDate(selectedDate: Long): Long

    @Insert
    fun insertStudySession(session: StudySession)
}