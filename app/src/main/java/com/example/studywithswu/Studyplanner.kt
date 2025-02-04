/*
package com.example.studywithswu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studywithswu.databinding.ActivityStudyplannerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudyPlanner : AppCompatActivity() {

    private lateinit var binding: ActivityStudyplannerBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var subjectsAdapter: SubjectsAdapter
    private val subjectsList = mutableListOf<Subject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyplannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchStudyData()
    }

    private fun setupRecyclerView() {
        subjectsAdapter = SubjectsAdapter(subjectsList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@StudyPlanner)
            adapter = subjectsAdapter
        }
    }

    private fun fetchStudyData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Log.e("StudyPlanner", "사용자가 로그인되지 않았습니다!")
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document == null || !document.exists()) {
                    Log.e("StudyPlanner", "Firestore 문서가 없음!")
                    return@addOnSuccessListener
                }

                val subjects = document.get("subjects")
                if (subjects is List<*>) {
                    subjectsList.clear()
                    for (subject in subjects) {
                        if (subject is Map<*, *>) {
                            val name = subject["name"] as? String ?: "Unknown"
                            val color = subject["color"] as? String ?: "#FFFFFF"
                            val timeMap = subject["time"] as? Map<String, Long> ?: emptyMap()
                            subjectsList.add(Subject(name, color, timeMap))
                        }
                    }
                    Log.d("StudyPlanner", "불러온 과목 개수: ${subjectsList.size}")
                    subjectsAdapter.notifyDataSetChanged()
                } else {
                    Log.e("StudyPlanner", "subjects 필드가 리스트가 아닙니다.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("StudyPlanner", "데이터 가져오기 실패: ${e.message}")
            }
    }
}
*/
