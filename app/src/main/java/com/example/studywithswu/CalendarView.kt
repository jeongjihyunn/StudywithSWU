package com.example.studywithswu

import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
            fetchStartStopTimes(selectedDate)
        }

        // 초기 날짜로 데이터 표시
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        fetchStudyData(currentDate)
    }

    private fun fetchStudyData(currentDate: String) {

    }

    // Firestore에서 데이터 가져오기
    /*private fun fetchStudyData(date: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjects = document.get("subjects") as? List<*>
                    val subjectList = subjects?.filterIsInstance<String>() ?: emptyList()
                    val lastStudyTime = subjectList.lastOrNull() ?: "0"

                    Log.d("bbb", "subjects 값: $subjects")

                    val longNumber:Long = lastStudyTime.toLongOrNull() ?: 0L
                    totalStudyTimeText.text = formatTime(longNumber).toString()

                } else {
                    totalStudyTimeText.text = "00:00:00"
                }
            }
            .addOnFailureListener { e ->
                Log.e("bbb", "데이터 가져오기 실패: ${e.message}")
                totalStudyTimeText.text = "오류 발생"
            }
    }*/

    private fun formatTime(time: Long): String {
        val seconds = (time / 1000) % 60
        val minutes = (time / 1000 / 60) % 60
        val hours = (time / 1000 / 60 / 60)
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun fetchStartStopTimes(date: String) {
        var userId = FirebaseAuth.getInstance().currentUser?.uid?: return
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Firestore에서 데이터를 읽기
                val start = document.get("start_times") as? ArrayList<String>
                val end = document.get("stop_times") as? ArrayList<String>
                val totalTime = document.getLong("totalTime_2025-02-04") ?: 0

                // startTimeText는 첫 번째 값을, endTimeText는 마지막 값을 표시
                totalStudyTimeText.text = formatTime(totalTime)
                startTimeText.text = start?.get(0) ?: "-"
                endTimeText.text = end?.lastOrNull() ?: "공부 중.."

                Log.d("aaa", "Total: ${document.get("totalTime_2025-02-04")}")
                Log.d("aaa", "Start Time: ${start?.get(0) ?: "없음"}")
                Log.d("aaa", "End Time: ${end?.lastOrNull() ?: "공부 중.."}")
            } else {
                startTimeText.text = "-"
                endTimeText.text = "공부 중.."
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
