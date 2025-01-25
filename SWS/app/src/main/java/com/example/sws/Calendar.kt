package com.example.sws

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class Calendar : AppCompatActivity() {
    private lateinit var studyDatabase: StudyDatabase
    private lateinit var calendarView: CalendarView
    private lateinit var studyTimeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        studyDatabase = StudyDatabase.getInstance(this)
        calendarView = findViewById(R.id.studyCalendar)
        studyTimeView = findViewById(R.id.studyTimeText)

        setupCalendar()
    }
    private fun setupCalendar(){
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

                CoroutineScope(Dispatchers.IO).launch {
                    val studyTime = getStudyTimeForDate(selectedDate)

                    withContext(Dispatchers.Main){
                        studyTimeView.text = "${formatStudyTime(studyTime)}"
                    }
                }
            }

    }

    private fun getStudyTimeForDate(date: LocalDate): Long {
        val epochDay = date.toEpochDay()
        return studyDatabase.studySessionDao().getStudyTimeByDate(epochDay)
    }

    private fun formatStudyTime(minutes: Long): String{
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return "${hours}시간 ${remainingMinutes}분"
    }
}