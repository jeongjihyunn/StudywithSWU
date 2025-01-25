package com.example.studywithswu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    lateinit var imgProfile: CircleImageView
    lateinit var edtNickname: EditText
    lateinit var edtIntro: EditText
    lateinit var edtMajor: EditText
    lateinit var edtGoaldate: EditText
    lateinit var edtGole: EditText
    lateinit var btnSave: Button

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        imgProfile = findViewById(R.id.imgProfile)
        edtNickname = findViewById(R.id.edtNickname)
        edtIntro = findViewById(R.id.edtIntro)
        edtMajor = findViewById(R.id.edtMajor)
        edtGoaldate = findViewById(R.id.edtGoaldate)
        edtGole = findViewById(R.id.edtGoal)
        btnSave = findViewById(R.id.btnSave)

        // 프로필 로드
        loadProfile()

        // 이미지뷰 클릭
        imgProfile.setOnClickListener {
            openImageChooser()
        }

        // 저장 버튼 클릭
        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {

            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        edtNickname.setText(document.getString("nickname") ?: "")
                        edtIntro.setText(document.getString("intro") ?: "")
                        edtMajor.setText(document.getString("major") ?: "")
                        edtGoaldate.setText(document.getString("goalDate") ?: "")
                        edtGole.setText(document.getString("goal") ?: "")

                        // 프로필 이미지 URL을 확인하고, 있으면 Glide로 로드, 없으면 기본 이미지 설정
                        val profileImageUrl = document.getString("profileImage")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .circleCrop()  // 원형으로 표시
                                .into(imgProfile)
                        } else {
                            imgProfile.setImageResource(R.drawable.default_profile_image)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "프로필 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val nickname = edtNickname.text.toString()
            val intro = edtIntro.text.toString()
            val major = edtMajor.text.toString()
            val goalDate = edtGoaldate.text.toString()
            val goal = edtGole.text.toString()

            // Firestore에 저장할 데이터
            val userProfile = hashMapOf(
                "nickname" to nickname,
                "intro" to intro,
                "major" to major,
                "goalDate" to goalDate,
                "goal" to goal
            )

            if (imageUri != null) {
                // 이미지가 선택된 경우, 업로드하고 URL을 Firestore에 저장
                uploadImageToFirebaseStorage(imageUri!!, userId) { imageUrl ->
                    userProfile["profileImage"] = imageUrl
                    saveToFirestore(userId, userProfile)
                }
            } else {
                // 이미지 없이 저장
                saveToFirestore(userId, userProfile)
            }
        } else {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirestore(userId: String, userProfile: Map<String, Any>) {
        firestore.collection("users").document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                // 수정 후 MyPage로 돌아가기
                val intent = Intent(this, MyPage::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "수정에 실패했습니다.: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri, userId: String, onSuccess: (String) -> Unit) {
        val fileReference = storageRef.child("profile_images/${userId}_${System.currentTimeMillis()}.jpg")

        fileReference.putFile(uri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "이미지 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imgProfile.setImageURI(imageUri)  // 선택한 이미지 바로 UI에 반영
        }
    }
}