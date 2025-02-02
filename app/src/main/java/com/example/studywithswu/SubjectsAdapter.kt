package com.example.studywithswu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubjectsAdapter(
    context: Context,
    private var subjects: List<Subject>
) : ArrayAdapter<Subject>(context, 0, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false)

        val subject = getItem(position)

        val subjectNameTextView: TextView = view.findViewById(R.id.subjectName)
        val subjectTimeTextView: TextView = view.findViewById(R.id.subjectTime)

        subject?.let {
            subjectNameTextView.text = it.name
            subjectTimeTextView.text = formatTime(it.time)  // 시간을 포맷팅하여 표시
        }

        return view
    }

    // 시간 포맷팅 함수
    private fun formatTime(timeMap: Map<String, Long>): String {
        val today = getCurrentDate()
        val timeForToday = timeMap[today] ?: 0L
        return formatTime(timeForToday)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun formatTime(time: Long): String {
        val hours = (time / 1000) / 3600
        val minutes = (time / 1000 % 3600) / 60
        val seconds = time / 1000 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // 새로운 과목 리스트로 업데이트
    fun updateSubjects(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()  // ListView 업데이트
    }
}
