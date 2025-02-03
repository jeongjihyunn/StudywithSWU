package com.example.studywithswu

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainScreen : AppCompatActivity(), WeeklyCalendarFragment.OnDateSelectedListener {
    private lateinit var dateTextView: TextView
    private lateinit var totalTimerTextView: TextView
    private lateinit var addButton: Button
    private lateinit var subjectsLayout: LinearLayout
    private lateinit var imageView: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null
    private var previousTotalTime: Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private var activeTimer: TimerRunnable? = null
    private var activeButton: Button? = null
    private val colors = listOf(
        "#FAE9E2",
        "#FCE4E2",
        "#EAEEE0",
        "#EBF6FA",
        "#EEE8E8",
        "#E9CCC4",
        "#E1D7CD",
        "#D7E0E5"
    )
    private val imageResources = listOf(
        R.drawable.char_original, R.drawable.char_4, R.drawable.char_8,
        R.drawable.char_12, R.drawable.char_16, R.drawable.char_20, R.drawable.char_24
    )
    private val timers = mutableListOf<TimerRunnable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Firebase 초기화
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 로그인한 사용자 ID 가져오기
        userId = firebaseAuth.currentUser?.uid

        // Firebase에서 과목 데이터 읽어오기
        initViews()
        loadUserData()

        // 툴바 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 숨기기

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_container, WeeklyCalendarFragment())
                .commit()
        }

        // 툴바 배경 투명 처리
        toolbar.setBackgroundColor(Color.TRANSPARENT)

        initViews()
        dateTextView.text = getCurrentDate()

        //loadUserDataFromFirestore()

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

    override fun onDateSelected(date: Date) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateStr = dateFormat.format(date)
        dateTextView.text = dateFormat.format(date)

        loadTotalTimeForDate(selectedDateStr)
    }

    private fun loadTotalTimeForDate(date: String) {
        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val totalTimeForDate = document.getLong("totalTime_$date") ?: 0L

                    // 선택한 날짜의 총 학습 시간을 UI에 반영
                    runOnUiThread {
                        totalTimerTextView.text = formatTime(totalTimeForDate)
                    }

                    println("Firestore에서 '$date' 총 학습 시간 불러오기 성공: $totalTimeForDate")
                }
            }.addOnFailureListener { e ->
                println("Firestore에서 '$date' 총 학습 시간 불러오기 실패: ${e.message}")
            }
        }
    }

    // 메뉴 인플레이트
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                showMenuOptions(findViewById(item.itemId)) // 클릭한 메뉴 버튼의 위치에서 팝업 표시 true
                true
            }

            R.id.action_studyplanner -> {
                val intent =
                    Intent(this, StudyPlanner::class.java) // 임시로 마이페이지로 이동할 수 있게 해놓음 여기 수정하면 됨
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMenuOptions(view: View) {
        val popup = PopupMenu(this, view) // 클릭한 위치에서 팝업 표시
        popup.menuInflater.inflate(R.menu.menu_options, popup.menu)

        // Android 10 이상에서 아이콘 표시
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.option_1 -> {
                    Toast.makeText(this, "'마이페이지' 선택", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MyPage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.option_2 -> {
                    Toast.makeText(this, "'캘린더' 선택", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CalendarView::class.java)
                    startActivity(intent)
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

    private fun loadUserData() {
        userId?.let { uid ->
            val today = getCurrentDate()
            val userRef = firestore.collection("users").document(uid)

            // Firestore에서 오늘 날짜의 총 학습 시간 불러오기
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val totalTimeToday = document.getLong("totalTime_$today") ?: 0L
                    previousTotalTime = totalTimeToday

                    runOnUiThread {
                        totalTimerTextView.text = formatTime(totalTimeToday)
                    }
                    println("Firestore에서 오늘 총 학습 시간 불러오기 성공: $totalTimeToday")
                }
            }.addOnFailureListener { e ->
                println("Firestore에서 총 학습 시간 불러오기 실패: ${e.message}")
            }

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList =
                        document.get("subjects") as? List<Map<String, String>> ?: emptyList()

                    runOnUiThread {
                        subjectsLayout.removeAllViews()  // 기존 뷰 초기화 후 추가 (중복 방지)
                        if (subjectsLayout.parent == null) {
                            findViewById<LinearLayout>(R.id.main).addView(subjectsLayout)  // subjectsLayout을 추가해 화면에 표시
                        }
                        for (subject in subjectsList) {
                            val subjectName = subject["name"] ?: "알 수 없음"
                            val color = subject["color"] ?: "#FFFFFF"
                            val time = (subject["time"] as? Long) ?: 0L
                            addNewSubjectTimer(subjectName, color)  // UI에 즉시 반영
                        }
                    }
                    println("Firestore에서 과목 목록 불러오기 성공")
                }
            }.addOnFailureListener { e ->
                println("Firestore에서 과목 목록 불러오기 실패: ${e.message}")
            }
        }
    }


    fun startTimer() {
        if (activeTimer == null) {
            activeTimer = TimerRunnable(totalTimerTextView) {
                updateTotalTime()
            }
            handler.post(activeTimer!!)
        }
        updateTotalTime()  // 타이머 시작할 때 즉시 총합 시간 반영
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

        val colorSelectionLayout =
            LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
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
        val hours = (totalTime / 1000 / 60 / 60).toInt()
        val imageIndex = when {
            hours >= 24 -> 6
            hours >= 20 -> 5
            hours >= 16 -> 4
            hours >= 12 -> 3
            hours >= 8 -> 2
            hours >= 4 -> 1
            else -> 0
        }
        imageView.setImageResource(imageResources[imageIndex])
    }

    // 앱 실행 시 Firestore에서 오늘 총합 시간 불러오기
    private fun loadTotalTimeForToday() {
        val today = getCurrentDate()

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val firestoreTotalTime = document.getLong("totalTime_$today") ?: 0L

                    // Firestore에서 불러온 값을 previousTotalTime에 저장하여 중복 추가 방지
                    previousTotalTime = firestoreTotalTime

                    // UI 업데이트 (이제 앱 실행 시 0이 보이지 않음)
                    runOnUiThread {
                        totalTimerTextView.text = formatTime(previousTotalTime)
                    }
                }
            }
        }
    }

    private fun updateTotalTime() {
        val today = getCurrentDate()
        val newElapsedTime = timers.sumOf { it.getElapsedTime() }  // 🔹 모든 타이머의 합산 값

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                val firestoreTotalTime = document.getLong("totalTime_$today") ?: 0L
                val updatedTotalTime = firestoreTotalTime + (newElapsedTime - previousTotalTime)

                userRef.set(mapOf("totalTime_$today" to updatedTotalTime), SetOptions.merge())
                    .addOnSuccessListener {
                        println("총합 시간 Firestore 업데이트 성공: $updatedTotalTime")
                    }
                    .addOnFailureListener { e ->
                        println("총합 시간 업데이트 실패: ${e.message}")
                    }

                previousTotalTime = newElapsedTime
                runOnUiThread {
                    totalTimerTextView.text = formatTime(updatedTotalTime)
                    updateImageBasedOnTime(updatedTotalTime)
                }
            }
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
            layoutParams =
                LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(16, 0, 16, 0)
                }
            setOnClickListener { showEditSubjectDialog(this) }
        }

        val timerTextView = TextView(this).apply {
            text = "00:00:00"
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 0, 16, 0) }
        }

        // 🔥 타이머 생성 및 리스트에 추가
        val timerRunnable = TimerRunnable(timerTextView) {
            updateTotalTime()
        }
        timers.add(timerRunnable)

        startStopButton.setOnClickListener {
            val currentTime = getCurrentTime()

            if (activeTimer == timerRunnable) { // 타이머가 실행 중인 경우 (정지)
                activeTimer?.let {
                    val elapsedTime = activeTimer!!.getElapsedTime()
                    activeTimer!!.stop()
                    activeTimer = null
                    activeButton = null
                    startStopButton.text = "시작"

                    // 🔥 Firestore에 종료 시간 저장
                    saveTimeToFirestore(subjectName, currentTime, isStart = false)

                    // 🔥 타이머 종료 시 Firestore 총 학습 시간 업데이트
                    updateSubjectTimeInFirestore(subjectName, elapsedTime)

                    // 🔥 종료 시간을 stop_times에 추가
                    updateStopTimesInFirestore(subjectName, currentTime)
                }
            } else { // 타이머 시작
                activeTimer?.stop()
                activeButton?.text = "시작"

                activeTimer = timerRunnable
                activeButton = startStopButton
                activeTimer!!.toggle(startStopButton)

                // 🔥 Firestore에 시작 시간 저장
                saveTimeToFirestore(subjectName, currentTime, isStart = true)

                // 🔥 시작 시간을 start_times에 추가
                updateStartTimesInFirestore(subjectName, currentTime)
            }
        }

        subjectLayout.addView(startStopButton)
        subjectLayout.addView(subjectTextView)
        subjectLayout.addView(timerTextView)
        subjectsLayout.addView(subjectLayout)

        saveSubjectToFirestore(userId!!, subjectName, color)
    }

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun saveSubjectToFirestore(userId: String, subjectName: String, color: String) {
        val userRef = firestore.collection("users").document(userId)
        val newSubject = mapOf("name" to subjectName, "color" to color, "time" to 0L) // 🔹 시간 필드 추가

        println("🔥 Firestore 과목 추가 시작: $subjectName")

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList = document.get("subjects") as? MutableList<Map<String, Any>>
                        ?: mutableListOf()
                    val subjectExists = subjectsList.any { it["name"] == subjectName }
                    if (subjectExists) {
                        println("⚠️ 이미 존재하는 과목: $subjectName (추가 X)")
                        return@addOnSuccessListener
                    }

                    userRef.update("subjects", FieldValue.arrayUnion(newSubject))
                        .addOnSuccessListener {
                            println("✅ Firestore에 과목 추가 성공: $subjectName")
                            loadUserData()
                        }
                        .addOnFailureListener { e ->
                            println("❌ Firestore에 과목 추가 실패: ${e.message}")
                        }
                } else {
                    userRef.set(mapOf("subjects" to listOf(newSubject)), SetOptions.merge())
                        .addOnSuccessListener {
                            println("✅ Firestore에 새 문서 생성 및 과목 추가 성공!")
                            loadUserData()
                        }
                        .addOnFailureListener { e ->
                            println("❌ Firestore에 새 문서 생성 실패: ${e.message}")
                        }
                }
            }
    }


    private class TimerRunnable(val timerTextView: TextView, private val onUpdate: () -> Unit) :
        Runnable {

        private val handler = Handler(Looper.getMainLooper())
        private var isRunning = false
        private var elapsedTime: Long = 0
        private var startTime: Long = 0

        override fun run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime
                timerTextView.text = formatTime(elapsedTime)
                onUpdate()  // 🔹 매 초마다 총합 시간 업데이트

                handler.postDelayed(this, 1000)  // 🔹 1초마다 실행
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
            onUpdate()  // 🔹 시작/중단 시 총합 시간 즉시 반영
        }

        fun stop() {
            isRunning = false
            handler.removeCallbacks(this)
            onUpdate()  // 🔹 타이머 중지 시 총합 시간 업데이트
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
        val subjectName = subjectTextView.text.toString()
        val today = getCurrentDate()

        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList =
                        document.get("subjects") as? List<Map<String, Any>> ?: emptyList()
                    val subject = subjectsList.find { it["name"] == subjectName }
                    val timeMap = subject?.get("time") as? Map<String, Long> ?: emptyMap()
                    val studyTimeForToday = timeMap[today] ?: 0L

                    val formattedTime = formatTime(studyTimeForToday)

                    // 🔥 UI에 적용
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("과목 수정/삭제")

                    val layout = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(50, 20, 50, 20)
                    }

                    val timeTextView = TextView(this).apply {
                        text = "금일 누적 시간: $formattedTime"
                        textSize = 16f
                        setPadding(0, 10, 0, 10)
                    }
                    layout.addView(timeTextView)

                    val input = EditText(this).apply {
                        setText(subjectName)
                    }
                    layout.addView(input)

                    builder.setView(layout)

                    builder.setPositiveButton("수정") { _, _ ->
                        val newSubjectName = input.text.toString().trim()
                        if (newSubjectName.isNotEmpty()) {
                            subjectTextView.text = newSubjectName
                            updateSubjectNameInFirestore(subjectName, newSubjectName)
                        } else {
                            Toast.makeText(this, "과목명을 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    builder.setNegativeButton("삭제") { _, _ ->
                        deleteSubject(subjectTextView)
                    }

                    builder.setNeutralButton("취소") { dialog, _ -> dialog.cancel() }

                    builder.show()
                }
            }
        }
    }

    private fun loadSubjectTimeForDate(
        subjectName: String,
        date: String,
        callback: (Long) -> Unit
    ) {
        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList =
                        document.get("subjects") as? List<Map<String, Any>> ?: emptyList()
                    val subject = subjectsList.find { it["name"] == subjectName }
                    val timeMap = subject?.get("time") as? Map<String, Long> ?: emptyMap()
                    val subjectTimeForDate = timeMap[date] ?: 0L

                    callback(subjectTimeForDate) // 콜백으로 데이터 전달
                }
            }
        }
    }

    private fun updateSubjectNameInFirestore(oldName: String, newName: String) {
        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList = document.get("subjects") as? MutableList<Map<String, Any>>
                        ?: mutableListOf()

                    val updatedSubjectsList = subjectsList.map {
                        if (it["name"] == oldName) {
                            it.toMutableMap().apply {
                                put("name", newName)  // 🔹 이름 변경
                            }
                        } else it
                    }

                    userRef.update("subjects", updatedSubjectsList)
                        .addOnSuccessListener {
                            println("✅ Firestore에서 과목명 변경 성공: $oldName → $newName")
                            Toast.makeText(this, "과목명이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            loadUserData()  // UI 갱신
                        }
                        .addOnFailureListener { e ->
                            println("❌ Firestore에서 과목명 변경 실패: ${e.message}")
                        }
                }
            }
        }
    }

    private fun updateSubjectTimeInFirestore(subjectName: String, elapsedTime: Long) {
        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            // Firestore에서 사용자의 과목 데이터를 가져오기
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList = document.get("subjects") as? MutableList<Map<String, Any>>
                        ?: mutableListOf()

                    val today = getCurrentDate() // 오늘 날짜 가져오기

                    // 해당 과목의 시간을 날짜별로 갱신
                    val updatedSubjectsList = subjectsList.map {
                        if (it["name"] == subjectName) {
                            it.toMutableMap().apply {
                                // 기존 시간 가져오기
                                val timeMap =
                                    it["time"] as? MutableMap<String, Long> ?: mutableMapOf()
                                val currentTime = timeMap[today] ?: 0L

                                // 날짜별로 시간 업데이트
                                timeMap[today] = currentTime + elapsedTime
                                put("time", timeMap)  // time 필드에 날짜별 시간 맵 저장
                            }
                        } else it
                    }

                    // Firestore에 갱신된 과목 데이터 저장
                    userRef.update("subjects", updatedSubjectsList)
                        .addOnSuccessListener {
                            println("✅ Firestore에서 과목 시간 날짜별로 저장 성공")
                        }
                        .addOnFailureListener { e ->
                            println("❌ Firestore에서 과목 시간 날짜별로 저장 실패: ${e.message}")
                        }
                }
            }
        }
    }

    fun updateStartTimesInFirestore(subjectName: String, startTime: String) {
        userId?.let { uid ->
            val sessionRef = firestore.collection("users").document(uid)

            sessionRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    // 문서가 없으면 새로 생성하고 시작 시간 추가
                    sessionRef.set(mapOf("start_times" to listOf(startTime)))
                } else {
                    // 문서가 있으면 시작 시간 추가
                    sessionRef.update("start_times", FieldValue.arrayUnion(startTime))
                }
            }.addOnFailureListener { e ->
                println("❌ Firestore에서 문서 조회 실패: ${e.message}")
            }
        }
    }


    fun updateStopTimesInFirestore(subjectName: String, stopTime: String) {
        userId?.let { uid ->
            val sessionRef = firestore.collection("users").document(uid)

            sessionRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    // 문서가 없으면 새로 생성하고 종료 시간 추가
                    sessionRef.set(mapOf("stop_times" to listOf(stopTime)))
                } else {
                    // 문서가 있으면 종료 시간 추가
                    sessionRef.update("stop_times", FieldValue.arrayUnion(stopTime))
                }
            }.addOnFailureListener { e ->
                println("❌ Firestore에서 문서 조회 실패: ${e.message}")
            }
        }
    }



    private fun loadSubjectTimeForDate(subjectName: String, date: String) {
        userId?.let { uid ->
            val userRef = firestore.collection("users").document(uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val subjectsList =
                        document.get("subjects") as? List<Map<String, Any>> ?: emptyList()
                    val subject = subjectsList.find { it["name"] == subjectName }
                    val timeMap = subject?.get("time") as? Map<String, Long> ?: emptyMap()
                    val subjectTimeForDate = timeMap[date] ?: 0L

                    // 화면에 표시할 수 있도록 포맷팅
                    val formattedTime = formatTime(subjectTimeForDate)
                    println("과목 '$subjectName'의 '$date' 총 시간: $formattedTime")
                }
            }
        }
    }

    private fun deleteSubject(subjectTextView: TextView) {
        val parentLayout = subjectTextView.parent as? LinearLayout
        val subjectName = subjectTextView.text.toString()

        if (parentLayout != null) {
            subjectsLayout.removeView(parentLayout)

            // 🔹 Firestore에서도 삭제
            userId?.let { uid ->
                val userRef = firestore.collection("users").document(uid)

                userRef.get().addOnSuccessListener { document ->
                    val subjectsList = document.get("subjects") as? MutableList<Map<String, String>>
                        ?: mutableListOf()

                    // 🔥 삭제할 과목 찾기
                    val updatedSubjectsList = subjectsList.filter { it["name"] != subjectName }

                    // 🔥 Firestore 업데이트 (과목 삭제 후 반영)
                    userRef.update("subjects", updatedSubjectsList)
                        .addOnSuccessListener {
                            println("✅ Firestore에서 과목 삭제 성공: $subjectName")
                            loadUserData()  // 🔥 Firestore에서 삭제 후 UI 업데이트
                        }
                        .addOnFailureListener { e ->
                            println("❌ Firestore에서 과목 삭제 실패: ${e.message}")
                        }
                }
            }

            Toast.makeText(this, "과목이 삭제되었습니다. Firestore에서도 삭제됨.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTimeToFirestore(subjectName: String, time: String, isStart: Boolean) {
        // subjectName과 time이 비어있지 않도록 체크
        if (subjectName.isEmpty() || time.isEmpty()) {
            println("❌ subjectName 또는 time이 비어있습니다.")
            return
        }

        userId?.let { uid ->
            val sessionRef = firestore.collection("users").document(uid)
                .collection("users").document(subjectName)

            val field = if (isStart) "start_times" else "stop_times"

            sessionRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    // 문서가 없으면 새로 생성, 기존 데이터에 병합
                    sessionRef.set(mapOf(field to listOf(time)), SetOptions.merge())
                } else {
                    // 문서가 존재하면 배열에 시간 추가
                    sessionRef.update(field, FieldValue.arrayUnion(time))
                }
            }.addOnFailureListener { e ->
                println("❌ Firestore에 $field 저장 실패: ${e.message}")
            }
        }
    }



}