package com.example.studywithswu

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studywithswu.databinding.ActivityStudyplannerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.studywithswu.Subject // Subject 클래스를 사용하기 위해 추가
import java.util.Calendar


class StudyPlanner : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var timetableLayout: LinearLayout
    private lateinit var dateLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studyplanner)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        timetableLayout = findViewById(R.id.timetableLayout)
        dateLayout = findViewById(R.id.dateLayout)

        userId = FirebaseAuth.getInstance().currentUser?.uid

        // Firestore에서 데이터를 가져오는 함수 호출
        loadUserData()
    }

    private fun loadUserData() {
        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList = document.get("subjects") as? List<Map<String, Any>> ?: emptyList()

                    // 날짜 영역에 오늘 날짜를 동적으로 추가
                    displayDates()

                    // 과목 리스트를 RecyclerView에 설정
                    setUpSubjects(subjectsList)

                    // 타임테이블을 설정
                    setUpTimetable(subjectsList)
                }
            }
        }
    }

    private fun displayDates() {
        val today = getCurrentDate() // 오늘 날짜 가져오기
        val dateTextView = TextView(this).apply {
            text = today
            textSize = 16f
            setPadding(16, 8, 16, 8)
        }
        dateLayout.addView(dateTextView)
    }

    private fun setUpSubjects(subjectsList: List<Map<String, Any>>) {
        val adapter = SubjectAdapter(subjectsList) { subject ->
            // 클릭 시 과목에 대한 세부정보를 표시하거나 타이머를 시작할 수 있음
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setUpTimetable(subjectsList: List<Map<String, Any>>) {
        // 타임테이블을 구현하기 위해 과목 리스트를 순차적으로 배치
        subjectsList.forEachIndexed { index, subject ->
            val color = subject["color"] as? String ?: "#FFFFFF"

            val subjectBlock = TextView(this).apply {
                text = subject["name"] as? String ?: "과목명"
                setBackgroundColor(Color.parseColor(color))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                setPadding(8, 8, 8, 8)
                gravity = Gravity.CENTER
            }

            timetableLayout.addView(subjectBlock)
        }
    }

    // 오늘 날짜 구하는 함수
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }

    // RecyclerView의 어댑터를 생성하는 클래스
    class SubjectAdapter(private val subjectsList: List<Map<String, Any>>, private val itemClickListener: (Map<String, Any>) -> Unit) :
        RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
            return SubjectViewHolder(view)
        }

        override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
            val subject = subjectsList[position]
            holder.bind(subject)
            holder.itemView.setOnClickListener {
                itemClickListener(subject)
            }
        }

        override fun getItemCount(): Int = subjectsList.size

        class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val subjectNameTextView: TextView = itemView.findViewById(R.id.subjectNameTextView)

            fun bind(subject: Map<String, Any>) {
                subjectNameTextView.text = subject["name"] as? String ?: "과목명"
            }
        }
    }
}

