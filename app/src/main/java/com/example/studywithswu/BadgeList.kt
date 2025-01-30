package com.example.studywithswu

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BadgeList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge_list)

        val hours = intent.getStringExtra("hours") ?: "0"
        val dates = intent.getStringArrayListExtra("dates") ?: arrayListOf()

        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val badgeList = findViewById<ListView>(R.id.badgeList)
        val badgeImage = findViewById<ImageView>(R.id.badgeImage) // 이미지 뷰

        txtTitle.text = "$hours 시간 공부한 날짜"

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dates)
        badgeList.adapter = adapter

        // 해당 hours에 맞는 이미지 설정
        val badgeImageRes = when (hours) {
            "4" -> R.drawable.time_4
            "8" -> R.drawable.time_8
            "12" -> R.drawable.time_12
            "16" -> R.drawable.time_16
            "20" -> R.drawable.time_20
            "24" -> R.drawable.time_24
            else -> R.drawable.time_4 // 기본값
        }

        badgeImage.setImageResource(badgeImageRes) // 이미지 설정
    }
}