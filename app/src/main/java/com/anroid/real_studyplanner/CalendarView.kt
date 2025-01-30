package com.anroid.real_studyplanner

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.real_studyplanner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class CalendarView : AppCompatActivity() {

    private lateinit var studyDatabase: StudyDatabase
    private lateinit var calendarView: CalendarView
    private lateinit var studyTimeTextView: TextView
    private lateinit var startTimeTextView: TextView
    private lateinit var endTimeTextView: TextView
    private lateinit var studyTimerStatus: TextView
    private lateinit var startStopTimerButton: Button

    private var isTimerRunning = false
    private var startTime: Long = 0L
    private var endTime: Long = 0L

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        studyDatabase = StudyDatabase.getInstance(this)
        calendarView = findViewById(R.id.studyCalendar)
        studyTimeTextView = findViewById(R.id.studyTimeTextView)
        startTimeTextView = findViewById(R.id.startTimeText)
        endTimeTextView = findViewById(R.id.endTimeText)

        setupCalendar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

            CoroutineScope(Dispatchers.IO).launch {
                val studyTime = getStudyTimeForDate(selectedDate)

                withContext(Dispatchers.Main) {
                    studyTimeTextView.text = "${formatStudyTime(studyTime)}"

                    // 날짜에 맞는 시작 시간과 종료 시간 가져오기
                    val (lastStartTime, lastEndTime) = getStartAndEndTimeForDate(selectedDate)

                    // 시작 시간과 종료 시간 텍스트 업데이트
                    startTimeTextView.text = "시작 시간: ${formatDate(lastStartTime ?: 0L)}"
                    if (isTimerRunning) {
                        endTimeTextView.text = "종료 시간: 공부중"
                    } else {
                        endTimeTextView.text = "종료 시간: ${formatDate(lastEndTime ?: 0L)}"
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStudyTimeForDate(date: LocalDate): Long {
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
        return calendar.timeInMillis / 1000 // milliseconds to seconds
    }


    private fun formatStudyTime(minutes: Long): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return "${hours}시간 ${remainingMinutes}분"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartAndEndTimeForDate(date: LocalDate): Pair<Long, Long> {
        // LocalDate를 Calendar로 변환
        val calendar = Calendar.getInstance().apply {
            set(date.year, date.monthValue - 1, date.dayOfMonth) // Calendar 월은 0부터 시작하므로 -1
        }

        // Calendar에서 timeInMillis를 가져와서 에포크 밀리초를 구함
        val epochMillis = calendar.timeInMillis

        // 데이터베이스에서 해당 밀리초 값을 사용하여 시작 시간과 종료 시간을 가져옵니다.
        val startTime = studyDatabase.studySessionDao().getLastStartTimeByDate(epochMillis) ?: 0L
        val endTime = studyDatabase.studySessionDao().getLastEndTimeByDate(epochMillis) ?: 0L

        return Pair(startTime, endTime)
    }


    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun startStopTimer(view: android.view.View) {
        if (isTimerRunning) {
            endTime = System.currentTimeMillis()
            studyTimerStatus.text = "타이머 종료"
            isTimerRunning = false
            startStopTimerButton.text = "타이머 시작"
        } else {
            startTime = System.currentTimeMillis()
            studyTimerStatus.text = "공부중"
            isTimerRunning = true
            startStopTimerButton.text = "타이머 종료"
        }
    }
}
