package com.example.studywithswu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class MyPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    lateinit var imgProfile: CircleImageView
    lateinit var txtNickname: TextView
    lateinit var txtIntro: TextView
    lateinit var txtMajor: TextView
    lateinit var txtGoaldate: TextView
    lateinit var txtGoal: TextView
    lateinit var btnEdit: Button

    lateinit var badge4: ImageView
    lateinit var badge8: ImageView
    lateinit var badge12: ImageView
    lateinit var badge16: ImageView
    lateinit var badge20: ImageView
    lateinit var badge24: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        imgProfile = findViewById(R.id.imgProfile)
        txtNickname = findViewById(R.id.txtNickname)
        txtIntro = findViewById(R.id.txtIntro)
        txtMajor = findViewById(R.id.txtMajor)
        txtGoaldate = findViewById(R.id.txtGoaldate)
        txtGoal = findViewById(R.id.txtGoal)
        btnEdit = findViewById(R.id.btnEdit)

        badge4 = findViewById(R.id.badge4)
        badge8 = findViewById(R.id.badge8)
        badge12 = findViewById(R.id.badge12)
        badge16 = findViewById(R.id.badge16)
        badge20 = findViewById(R.id.badge20)
        badge24 = findViewById(R.id.badge24)

        //프로필 로드
        loadProfile()

        //뱃지 업데이트
        updateBadges()

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }

        // 뱃지 클릭 시 해당 데이터 넘기기
        val badgeMap = mapOf(
            badge4 to "4",
            badge8 to "8",
            badge12 to "12",
            badge16 to "16",
            badge20 to "20",
            badge24 to "24"
        )

        badgeMap.forEach { (badgeView, hour) ->
            badgeView.setOnClickListener {
                val userId = auth.currentUser?.uid ?: return@setOnClickListener
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val badgeData = document.get("badge") as? Map<String, List<String>> ?: emptyMap()
                        val dateList = badgeData[hour] ?: emptyList()

                        val intent = Intent(this, BadgeList::class.java)
                        intent.putStringArrayListExtra("dates", ArrayList(dateList))
                        intent.putExtra("hours", hour)
                        startActivity(intent)
                    }
            }
        }
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    txtNickname.text = document.getString("nickname") ?: ""
                    txtIntro.text = document.getString("intro") ?: ""
                    txtMajor.text = document.getString("major") ?: ""
                    txtGoaldate.text = document.getString("goalDate") ?: ""
                    txtGoal.text = document.getString("goal") ?: ""
                }

                // 프로필 이미지 로드
                val profileImageUrl = document.getString("profileImage")
                val imgProfile: ImageView = findViewById(R.id.imgProfile)

                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(profileImageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(imgProfile)
                } else {
                    imgProfile.setImageResource(R.drawable.default_profile_image)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "프로필 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateBadges() {
        val userId = auth.currentUser?.uid ?: return
        val badgeMilestones = listOf(4, 8, 12, 16, 20, 24)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val totalTimeField = "totalTime_$currentDate" // 날짜별 totalTime 필드명

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val totalTime = document.getLong(totalTimeField) ?: 0  // 날짜별 totalTime 가져오기
                    val totalHours = totalTime / 1000 / 3600
                    val badgeData = document.get("badge") as? Map<String, List<String>> ?: emptyMap()

                    val updates = mutableMapOf<String, Any>()
                    for (hour in badgeMilestones) {
                        if (totalHours >= hour) {
                            val existingDates = badgeData[hour.toString()] ?: emptyList()
                            if (!existingDates.contains(currentDate)) {
                                updates["badge.$hour"] = existingDates + currentDate
                            }
                        }
                    }

                    if (updates.isNotEmpty()) {
                        firestore.collection("users").document(userId).update(updates)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
        updateBadges()  // 새로고침 시 뱃지 업데이트
    }
}
