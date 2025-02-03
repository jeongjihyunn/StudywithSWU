package com.example.studywithswu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.Calendar
import android.graphics.Color

class WeeklyCalendarRecycler (private var dates: List<Date>,
                              private val onDateSelected: (Date) -> Unit):
    RecyclerView.Adapter<WeeklyCalendarRecycler.CalendarViewHolder>(){

    private var selectedDate: Date = Calendar.getInstance().time // 기본 선택 날짜 = 오늘
    private var selectedDayOfWeek: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    private var isWeekChanged: Boolean = false

    inner class CalendarViewHolder(view: View): RecyclerView.ViewHolder(view){
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)


        init {
            view.setOnClickListener {
                selectedDate = dates[adapterPosition]
                selectedDayOfWeek = Calendar.getInstance().apply {
                    time = selectedDate
                }.get(Calendar.DAY_OF_WEEK)
                isWeekChanged = false
                onDateSelected(selectedDate)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_calendar_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = dates[position]

        // MM/dd 형식 날짜 표시
        val dateFormat = SimpleDateFormat("M/d", Locale.KOREAN)
        // 요일만 표시
        val dayFormat = SimpleDateFormat("EEE", Locale.KOREAN)

        holder.dateTextView.text = dateFormat.format(date)
        holder.dayTextView.text = dayFormat.format(date)

        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance().apply { time = date }

        // 날짜 기본 설정
        holder.dateTextView.setTextColor(Color.BLACK)
        holder.dateTextView.setBackgroundColor(Color.TRANSPARENT)

        // 오늘 날짜
        val isToday = today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH)

        if (isToday) {
            // 오늘 날짜 색상 변경
            holder.dateTextView.setTextColor(Color.parseColor("#8A2926"))
        }

        // 주간 이동 시에는 요일 기준으로, 그 외에는 선택된 날짜 기준으로 처리
        if (isWeekChanged && dateCalendar.get(Calendar.DAY_OF_WEEK) == selectedDayOfWeek) {
            selectedDate = date
            holder.dateTextView.setBackgroundColor(Color.parseColor("#d4d4d4"))
        } else if (!isWeekChanged && isSameDay(date, selectedDate)) {
            holder.dateTextView.setBackgroundColor(Color.parseColor("#d4d4d4"))
        }
    }

    override fun getItemCount() = dates.size

    fun updateDates(newDates: List<Date>) {
        isWeekChanged = true
        dates = newDates
        // 선택된 요일에 해당하는 날짜 찾기
        val newSelectedDate = dates.find { date ->
            Calendar.getInstance().apply {
                time = date
            }.get(Calendar.DAY_OF_WEEK) == selectedDayOfWeek
        }
        // 새로운 날짜가 있으면 선택
        newSelectedDate?.let {
            selectedDate = it
            onDateSelected(it)
        }
        notifyDataSetChanged()
    }

    // 선택한 날짜 외부에서 수정
    fun setSelectedDate(date: Date) {
        selectedDate = date
        selectedDayOfWeek = Calendar.getInstance().apply {
            time = date
        }.get(Calendar.DAY_OF_WEEK)
        isWeekChanged = false
        notifyDataSetChanged()
    }

    // 날짜 비교
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}