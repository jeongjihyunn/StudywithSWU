package com.example.studywithswu

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SubjectsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var totalStudyTimeText: TextView
    private lateinit var startTimes: MutableList<String>
    private lateinit var stopTimes: MutableList<String>
    private lateinit var subjectTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studyplanner) }}// XML 파일과 연결

//        totalStudyTimeText = findViewById(R.id.tv_total_study_time)
//        subjectTextView = findViewById(R.id.tvSubjectName)
//
//        startTimes = mutableListOf()
//        stopTimes = mutableListOf()
//
//        fetchSubjects()
//        fetchTimetableData()
//    }
//
//    private fun fetchSubjects() {
//        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        db.collection("subjects").get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val name = document.getString("name") ?: "Unknown"
//                    val color = document.getString("color") ?: "#FFFFFF"
//                    val timeMap = document.get("time") as? Map<String, Long> ?: emptyMap()
//                    val studyTime = timeMap[currentDate] ?: 0
//
//                    Log.d("Firestore", "과목: $name, 색상: $color, 공부 시간: $studyTime 초")
//
//                    // UI 업데이트 (예제: TextView에 설정)
//                    subjectTextView.append("$name\n")
//
//                    // 총 공부 시간 업데이트
//                    val totalTime = totalStudyTimeText.text.toString().toIntOrNull() ?: 0
//                    totalStudyTimeText.text = (totalTime + studyTime).toString()
//                }
//            }
//            .addOnFailureListener { e -> Log.e("Firestore", "과목 데이터를 가져오는 데 실패함", e) }
//    }
//
//    private fun fetchTimetableData() {
//        db.collection("timetable").document("session")
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    startTimes = (document.get("start_times") as? List<String>)?.toMutableList() ?: mutableListOf()
//                    stopTimes = (document.get("stop_times") as? List<String>)?.toMutableList() ?: mutableListOf()
//
//                    Log.d("Firestore", "시작 시간: $startTimes")
//                    Log.d("Firestore", "종료 시간: $stopTimes")
//                }
//            }
//            .addOnFailureListener { e -> Log.e("Firestore", "타임테이블 데이터를 가져오는 데 실패함", e) }
//    }
/*}*/
