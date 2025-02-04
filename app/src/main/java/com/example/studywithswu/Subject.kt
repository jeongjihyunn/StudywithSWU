package com.example.studywithswu

data class Subject(
    val name: String,  // 과목 이름
    val color: String,  // 과목 색상 (기본값: 흰색)
    val time: Map<String, Long> = emptyMap()  // 날짜별 시간 (Map 형태로 저장)
)


