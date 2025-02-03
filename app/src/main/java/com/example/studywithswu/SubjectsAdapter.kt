package com.example.studywithswu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studywithswu.databinding.ItemSubjectBinding

// Adapter에 List<Subject>을 전달받아서 RecyclerView에 표시
class SubjectsAdapter(private val subjects: List<Subject>) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.bind(subject)
    }

    override fun getItemCount(): Int = subjects.size

    // ViewHolder에서 데이터를 바인딩합니다.
    inner class SubjectViewHolder(private val binding: ItemSubjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subject: Subject) {
            // 과목명 표시
            binding.subjectNameTextView.text = subject.name

            // 과목 색상 표시
            binding.subjectColorView.setBackgroundColor(Color.parseColor(subject.color))

            // 타이머 시간 표시 (2025-02-03 날짜 기준으로 시간 가져오기)
            val time = subject.time["2025-02-03"] ?: 0L  // 예시로 "2025-02-03" 날짜를 기준으로 시간 표시
            binding.timeTextView.text = formatTime(time)
        }
    }

    // 시간 포맷을 (HH:mm:ss) 형식으로 변환하는 함수
    private fun formatTime(timeInMillis: Long): String {
        val hours = (timeInMillis / (1000 * 60 * 60)).toInt()
        val minutes = (timeInMillis / (1000 * 60) % 60).toInt()
        val seconds = (timeInMillis / 1000 % 60).toInt()
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
