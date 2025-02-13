package com.example.studywithswu

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.widget.Toolbar

class MainScreen : AppCompatActivity() {
    private lateinit var dateTextView: TextView
    private lateinit var totalTimerTextView: TextView
    private lateinit var addButton: Button
    private lateinit var subjectsLayout: LinearLayout
    private lateinit var imageView: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private var activeTimer: TimerRunnable? = null
    private var activeButton: Button? = null
    private val colors = listOf("#FAE9E2", "#FCE4E2", "#EAEEE0", "#EBF6FA", "#EEE8E8", "#E9CCC4", "#E1D7CD", "#D7E0E5")
    private val imageResources = listOf(R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e)
    private val timers = mutableListOf<TimerRunnable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        // 툴바 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 숨기기

        // 툴바 배경 투명 처리
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        initViews()
        dateTextView.text = getCurrentDate()
        addButton.setOnClickListener {
            if (subjectsLayout.parent == null) {
                findViewById<LinearLayout>(R.id.main).addView(subjectsLayout)
            }
            showAddSubjectDialog()
        }

        setSupportActionBar(toolbar)  // Activity의 ActionBar로 설정

        // ActionBar의 타이틀을 없애고 싶으면
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    // 메뉴 인플레이트
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                val view = findViewById<View>(R.id.toolbar) // 툴바를 기준으로 PopupMenu 표시
                showMenuOptions(view)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showMenuOptions(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_options, popup.menu)

        // 오른쪽 상단에 메뉴 정렬 (Android 10 이상에서 필요)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.option_1 -> {
                    Toast.makeText(this, "'마이페이지' 선택", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.option_2 -> {
                    Toast.makeText(this, "'통계' 선택", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }



    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        dateTextView = findViewById(R.id.dateTextView)
        totalTimerTextView = findViewById(R.id.totalTimerTextView)
        addButton = findViewById(R.id.addButton)
        subjectsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun showAddSubjectDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("새로운 과목 추가")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val input = EditText(this).apply { hint = "과목명을 입력하세요" }
        layout.addView(input)

        val colorSelectionLayout = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        var selectedColor = colors[0]

        colors.forEach { color ->
            val colorButton = Button(this).apply {
                setBackgroundColor(Color.parseColor(color))
                layoutParams = LinearLayout.LayoutParams(80, 80).apply { setMargins(10, 0, 10, 0) }
                setOnClickListener { selectedColor = color }
            }
            colorSelectionLayout.addView(colorButton)
        }
        layout.addView(colorSelectionLayout)

        builder.setView(layout)
        builder.setPositiveButton("추가") { _, _ ->
            val subjectName = input.text.toString().trim()
            if (subjectName.isNotEmpty()) {
                addNewSubjectTimer(subjectName, selectedColor)
            } else {
                Toast.makeText(this, "과목명을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun updateImageBasedOnTime(totalTime: Long) {
        val seconds = (totalTime / 1000).toInt()
        val imageIndex = when {
            seconds >= 20 -> 4
            seconds >= 15 -> 3
            seconds >= 10 -> 2
            seconds >= 5 -> 1
            else -> 0
        }
        imageView.setImageResource(imageResources[imageIndex])
    }

    private fun updateTotalTime() {
        val totalTime = timers.sumOf { it.getElapsedTime() }
        runOnUiThread {
            totalTimerTextView.text = formatTime(totalTime)
            updateImageBasedOnTime(totalTime)
        }
    }

    private fun formatTime(time: Long): String {
        val seconds = (time / 1000) % 60
        val minutes = (time / 1000 / 60) % 60
        val hours = (time / 1000 / 60 / 60)
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun addNewSubjectTimer(subjectName: String, color: String) {
        val subjectLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 16) }
        }

        val startStopButton = Button(this).apply {
            text = "시작"
            setBackgroundColor(Color.parseColor(color))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 0, 16, 0) }
        }

        val subjectTextView = TextView(this).apply {
            text = subjectName
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(16, 0, 16, 0)
            }
            setOnClickListener { showEditSubjectDialog(this) }  // 클릭 시 과목명 수정 다이얼로그 실행
        }

        val timerTextView = TextView(this).apply {
            text = "00:00:00"
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 0, 16, 0) }
        }

        val timerRunnable = TimerRunnable(timerTextView) {
            updateTotalTime()
        }
        timers.add(timerRunnable)

        startStopButton.setOnClickListener {
            if (activeTimer != null && activeTimer!!.isRunning() && activeTimer != timerRunnable) {
                activeTimer!!.stop()
                activeButton?.text = "시작"
            }
            if (activeTimer == timerRunnable) {
                activeTimer!!.stop()
                startStopButton.text = "시작"
                activeTimer = null
                activeButton = null
            } else {
                activeTimer = timerRunnable
                activeButton = startStopButton
                activeTimer!!.toggle(startStopButton)
            }
        }

        subjectLayout.addView(startStopButton)
        subjectLayout.addView(subjectTextView)
        subjectLayout.addView(timerTextView)
        subjectsLayout.addView(subjectLayout)
    }

    private class TimerRunnable(private val timerTextView: TextView, private val onUpdate: () -> Unit) : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var isRunning = false
        private var elapsedTime: Long = 0
        private var startTime: Long = 0

        override fun run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                timerTextView.text = formatTime(elapsedTime)
                onUpdate()
                handler.postDelayed(this, 100)
            }
        }

        fun toggle(button: Button) {
            if (isRunning) {
                isRunning = false
                button.text = "시작"
                handler.removeCallbacks(this)
            } else {
                isRunning = true
                startTime = System.currentTimeMillis() - elapsedTime
                button.text = "중단"
                handler.post(this)
            }
        }

        fun stop() {
            isRunning = false
            handler.removeCallbacks(this)
            onUpdate()
        }

        fun isRunning(): Boolean = isRunning

        fun getElapsedTime(): Long = elapsedTime

        private fun formatTime(time: Long): String {
            val seconds = (time / 1000) % 60
            val minutes = (time / 1000 / 60) % 60
            val hours = (time / 1000 / 60 / 60)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
    private fun showEditSubjectDialog(subjectTextView: TextView) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("과목명 수정")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val input = EditText(this).apply {
            setText(subjectTextView.text.toString())
        }
        builder.setView(input)

        builder.setPositiveButton("수정") { _, _ ->
            val newSubjectName = input.text.toString().trim()
            if (newSubjectName.isNotEmpty()) {
                subjectTextView.text = newSubjectName
            } else {
                Toast.makeText(this, "과목명을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}
