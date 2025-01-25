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

class MyPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    lateinit var imgProtile : CircleImageView
    lateinit var txtNickname : TextView
    lateinit var txtIntro : TextView
    lateinit var txtMajor : TextView
    lateinit var txtGoaldate : TextView
    lateinit var txtGoal : TextView
    lateinit var btnEdit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance() //Firebase에서 인스턴스를 가져올 것
        firestore = FirebaseFirestore.getInstance()

        imgProtile = findViewById(R.id.imgProfile)
        txtNickname = findViewById(R.id.txtNickname)
        txtIntro = findViewById(R.id.txtIntro)
        txtMajor = findViewById(R.id.txtMajor)
        txtGoaldate = findViewById(R.id.txtGoaldate)
        txtGoal = findViewById(R.id.txtGoal)
        btnEdit = findViewById(R.id.btnEdit)

        loadProfile()

        btnEdit.setOnClickListener{
            intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun loadProfile(){
        val userId = auth.currentUser?.uid
        if (userId != null) {
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

                    // 프로필 이미지 URL을 Firestore에서 가져오기
                    val profileImageUrl = document.getString("profileImage")
                    val imgProfile: ImageView = findViewById(R.id.imgProfile)  // ImageView 찾기

                    // 프로필 이미지 URL이 있으면 Glide로 로드, 없으면 기본 이미지로 설정
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)  // 캐시를 사용하지 않도록 설정
                            .skipMemoryCache(true)  // 메모리 캐시 사용 안함
                            .circleCrop()  // 원형으로 표시
                            .into(imgProfile)

                    } else {
                        imgProfile.setImageResource(R.drawable.default_profile_image)  // 기본 이미지 설정
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "프로필을 로드하는데 실패했습니다.: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()  // 새로운 데이터가 로드되도록
    }

}