package com.example.StudyPlanner

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // 날짜 포맷과 초기 날짜 변수
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 최상위 레이아웃 생성
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
        }
        setContentView(rootLayout)

        // 날짜 관련 UI 요소 동적 생성
        val dateTextView = TextView(this).apply {
            textSize = 20f
            text = dateFormat.format(currentDate.time)
            gravity = Gravity.CENTER
        }
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        val prevButton = Button(this).apply { text = "Previous" }
        val nextButton = Button(this).apply { text = "Next" }
        buttonLayout.addView(prevButton)
        buttonLayout.addView(nextButton)

        rootLayout.addView(dateTextView)
        rootLayout.addView(buttonLayout)

        // 리스트뷰 동적 생성 및 데이터 연결
        val subjectListView = ListView(this)
        val subjects = listOf(
            Subject("컴퓨터시스템기초", "02:51:46", "#FFC0C0"),
            Subject("자료구조", "01:34:22", "#FFFFC0"),
            Subject("미디어통신", "01:29:05", "#C0FFC0")
        )
        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, subjects.map {
            "${it.name}\n${it.time}"
        })
        subjectListView.adapter = subjectAdapter

        rootLayout.addView(subjectListView)

        // 타임테이블 동적 생성
        val timetableGridLayout = GridLayout(this).apply {
            rowCount = subjects.size
            columnCount = 1
        }
        rootLayout.addView(timetableGridLayout)
        populateTimetable(timetableGridLayout, subjects)

        // 버튼 클릭 이벤트
        prevButton.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_MONTH, -1)
            dateTextView.text = dateFormat.format(currentDate.time)
        }
        nextButton.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
            dateTextView.text = dateFormat.format(currentDate.time)
        }
    }

    // 타임테이블에 시간 블록 추가
    private fun populateTimetable(timetableGridLayout: GridLayout, subjects: List<Subject>) {
        timetableGridLayout.removeAllViews() // 기존 시간표 초기화
        subjects.forEachIndexed { index, subject ->
            val timeBlock = createTimeBlock(subject.color, index)
            timetableGridLayout.addView(timeBlock)
        }
    }

    // 타임블록 생성 함수
    private fun createTimeBlock(color: String, rowIndex: Int): View {
        val block = View(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(rowIndex, 1)
                columnSpec = GridLayout.spec(0)
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = 150
            }
            setBackgroundColor(Color.parseColor(color))
        }
        return block
    }

    data class Subject(val name: String, val time: String, val color: String)
}
