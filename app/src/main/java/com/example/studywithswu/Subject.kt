package com.example.studywithswu

data class Subject(
    val name: String,
    val color: String,
    val time: Map<String, Long> // 날짜별 공부 시간
)
