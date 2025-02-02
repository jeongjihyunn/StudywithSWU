package com.example.studywithswu

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.security.auth.Subject

class Studyplanner : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var subjectListView: ListView
    private lateinit var totalStudyTimeTextView: TextView
    private lateinit var subjectsAdapter: SubjectsAdapter
    private var subjectListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // XML 레이아웃 파일과 연결

        //FirebaseFirestore 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        //view 초기화
        subjectListView = findViewById(R.id.subjectListView)
        totalStudyTimeTextView = findViewById(R.id.totalStudyTimeTextView)

        //Adapter 설정
        subjectsAdapter = SubjectsAdapter(this, emptyList())
        subjectListView.adapter = subjectsAdapter

        //과목 데이터 실시간 리스닝
        listenToSubjectUpdates()

        // 타임테이블 셀 추가
        val gridLayout = findViewById<GridLayout>(R.id.timetableGrid)
        gridLayout.removeAllViews()

        val rowCount = 7 // 주간 기준으로 7일 (월~일)
        val columnCount = 24 // 하루 24시간

        // 타임테이블 그리드 구성
        for (row in 0..rowCount) { // 0부터 시작해서 시간 표시 포함
            for (col in 0..columnCount) { // 0부터 시작해서 시간 표시 포함
                val textView = TextView(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        rowSpec = GridLayout.spec(row, 1f)
                        columnSpec = GridLayout.spec(col, 1f)
                        width = 0
                        height = 0
                        setMargins(1, 1, 1, 1) // 셀 간격
                    }
                    gravity = Gravity.CENTER
                    textSize = 12f
                    setBackgroundColor(Color.WHITE) // 기본 배경색

                    // 첫 번째 행: 시간 표시
                    if (row == 0) {
                        text = if (col == 0) "" else "${col - 1}:00"
                        setBackgroundColor(Color.LTGRAY)
                    }
                    // 나머지 셀
                    else {
                        text = "" // 빈 셀
                        setBackgroundColor(Color.parseColor("#F0F0F0"))
                    }
                }
                gridLayout.addView(textView)
            }
        }
    }

    //Firestore에서 과목 데이터를 실시간으로 가져오는 함수
    private fun listenToSubjectUpdates() {
        val userID = "사용자ID" //실제 사용자 ID로 변경 필요
        val userRef = firestore.collection("users").document(userID)

        subjectListener = userRef.addSnapshotListener { document, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {
                val subjectsList = document.get("subjects") as? List<Map<String, Any>> ?: emptyList()

                //과목 정보를 Subject 데이터 클래스로 변환
                val subjects = subjectsList.map { subjectMap ->
                    val name = subjectMap["name"] as? String ?: ""
                    val color = subjectMap["color"] as? String ?: ""
                    val time = subjectMap["time"] as? Map<String, Long> ?: emptyMap()
                    Subject(name, color, time)
                }

                //Adapter에 과목 리스트 업데이트
                subjectsAdapter.updateSubjects(subjects)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Activity가 종료되면 Firestore 리스너 해제
        subjectListener?.remove()
    }
}
