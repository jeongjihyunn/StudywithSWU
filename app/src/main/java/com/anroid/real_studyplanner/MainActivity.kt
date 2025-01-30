package com.android.real_studyplanner

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // XML 레이아웃 파일과 연결

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
}
