package com.example.studywithswu

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date


class WeeklyCalendarFragment : Fragment() {
    private lateinit var weeklyCalendar: RecyclerView
    private lateinit var weeklyCalendarRecycler: WeeklyCalendarRecycler
    private var calendar = Calendar.getInstance()
    private var selectedDate: Date = calendar.time // 기본 선택 날짜 = 오늘


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weekly_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weeklyCalendar = view.findViewById(R.id.calendarRecyclerView)
        weeklyCalendar.layoutManager = GridLayoutManager(requireContext(), 7)

        // 화살표 버튼 설정
        val prevWeekButton: ImageButton = view.findViewById(R.id.prevWeekButton)
        val nextWeekButton: ImageButton = view.findViewById(R.id.nextWeekButton)

        // 이전 주 버튼 클릭 리스너
        prevWeekButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateCalendar()
        }

        // 다음 주 버튼 클릭 리스너
        nextWeekButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateCalendar()
        }

        updateCalendar()

    }

    private fun updateCalendar(){
        val weekDates = getWeekDates()
        weeklyCalendarRecycler = WeeklyCalendarRecycler(weekDates){ date ->
            selectedDate = date
        }
        weeklyCalendar.adapter = weeklyCalendarRecycler
    }

    private fun getWeekDates(): List<Date>{
        val weekDates = mutableListOf<Date>()
        val currentDate = calendar.time
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)

        repeat(7){
            weekDates.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return weekDates
    }
}