package com.example.studywithswu

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CalendarView : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var totalStudyTimeText: TextView
    private lateinit var startTimeText: TextView
    private lateinit var endTimeText: TextView

    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스 생성
    private lateinit var studyDatabase: StudyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        // 뷰 초기화
        calendarView = findViewById(R.id.studyCalendar)
        totalStudyTimeText = findViewById(R.id.totalTimeTextView)
        startTimeText = findViewById(R.id.startTimeTextView)
        endTimeText = findViewById(R.id.endTimeTextView)

        studyDatabase = StudyDatabase.getInstance(this)

        // 캘린더 날짜 선택 리스너
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchStudyData(selectedDate) // Firestore에서 데이터 가져오기
        }

        // 초기 날짜로 데이터 표시
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        fetchStudyData(currentDate)
    }

    // Firestore에서 데이터 가져오기
    private fun fetchStudyData(date: String) {
        db.collection("users").document(date).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Firestore 데이터 읽기
                    val totalTime= document.getString("subjects") ?: "00:00:00"

                    // 데이터 업데이트
                    totalStudyTimeText.text = totalTime
                } else {
                    // 해당 날짜에 데이터가 없을 경우
                    totalStudyTimeText.text = "00:00:00"
                }
            }
            .addOnFailureListener {
                // 에러 처리
                totalStudyTimeText.text = "오류 발생"
            }
    }

    private fun fetchStartStopTimes(date: String) {
        db.collection("users").document(date).get().addOnSuccessListener { document ->
            if(document.exists()) {
                // Firestore에서 "start_times"와 "stop_times" 필드 값 읽기
                val startTimes = document.getString("start_times") ?: "-"
                val stopTimes = document.getString("stop_times") ?: "공부 중.."
                // startTimeText와 endTimeText 업데이트
                startTimeText.text = startTimes
                endTimeText.text = stopTimes
            } else {
                //해당 날짜에 데이터가 없을 경우
                startTimeText.text="-"
                endTimeText.text="공부 중.."
            }
        }.addOnFailureListener {
            // 에러 발생 시 처리
            startTimeText.text = "-"
            endTimeText.text = "오류 발생"
        }
    }

    // 캘린더 날짜 선택 리스너에서 사용하는 방법
    private fun setupCalendar(year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val totalStudyTime = getTotalStudyTimeForDate(selectedDate)

            withContext(Dispatchers.Main) {
                totalStudyTimeText.text = formatStudyTime(totalStudyTime)
            }
        }
    }

    // LocalDate 대신 Calendar 객체 사용
    private fun getTotalStudyTimeForDate(calendar: Calendar): Long {
        val epochDay = calendar.timeInMillis / (1000 * 60 * 60 * 24)
        return studyDatabase.studySessionDao().getStudyTimeByDate(epochDay)
    }

    private fun formatStudyTime(minutes: Long): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return "${hours}시간 ${remainingMinutes}분"
    }
}
