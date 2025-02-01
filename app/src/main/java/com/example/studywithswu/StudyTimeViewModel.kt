package com.example.studywithswu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudyTimeViewModel: ViewModel() {
    // 선택된 날짜를 관리하는 LiveData
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    // 학습 시간 데이터를 관리하는 LiveData
    private val _studyTimeData = MutableLiveData<StudyTimeData?>()
    val studyTimeData: LiveData<StudyTimeData?> = _studyTimeData


    data class StudyTimeData(
        val totalTime: Long,
        val subjects: List<SubjectTime>
    )


    data class SubjectTime(
        val name: String,
        val time: Long
    )

    // Firebase 인스턴스
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    fun loadStudyTimeForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return
        _selectedDate.value = date

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // totalTime_{date} 형식으로 저장된 해당 날짜의 총 학습시간 조회
                    val totalTime = document.getLong("totalTime_$date") ?: 0L

                    // subjects 컬렉션에서 과목별 학습시간 조회
                    val subjectsList = document.get("subjects") as? List<Map<String, Any>> ?: emptyList()
                    val subjectTimes = subjectsList.map { subject ->
                        SubjectTime(
                            name = subject["name"] as String,
                            time = subject["time"] as Long
                        )
                    }

                    _studyTimeData.value = StudyTimeData(totalTime, subjectTimes)
                } else {
                    _studyTimeData.value = null // 데이터가 없는 경우
                }
            }
    }
}