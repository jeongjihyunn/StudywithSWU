package com.anroid.real_studyplanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,  // 날짜 (epoch day)
    val startTime: Long,  // 공부 시작 시간 (epoch time)
    val endTime: Long?,  // 공부 종료 시간 (epoch time, null 가능)
    val duration: Long  // 공부 시간 (분 단위 등)
)
