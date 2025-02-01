package com.example.studywithswu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth //Firebase를 사용하는 권한
    private lateinit var firestore : FirebaseFirestore

    lateinit var edtName : EditText
    lateinit var edtEmail : EditText
    lateinit var edtPassword : EditText
    lateinit var btnSignIn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance() //Firebase에서 인스턴스를 가져올 것
        firestore = FirebaseFirestore.getInstance()

        btnSignIn = findViewById(R.id.btnSignIn)

        btnSignIn.setOnClickListener{
            registerUser()
        }
    }
    private fun registerUser(){

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)

        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()
        val name = edtName.text.toString()

        auth.createUserWithEmailAndPassword(email , password) //firebase 권한으로 email, password를 만든다.
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Firestore에 사용자 세부 정보 저장
                    saveUserData(name, email)
                    // 회원가입 성공 메시지 표시
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    // 메인 액티비티로 이동
                    navigateToMainActivity()
                } else {
                    // 회원가입 실패 시 사용자에게 메시지 표시
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(edtName:String, edtEmail:String){
        //firebase에 저장
        val user = hashMapOf( //해시맵으로 username, email 필드에 저장
            "username" to edtName,
            "email" to edtEmail
        )
        // 생성된 ID로 새 문서 추가
        firestore.collection("users") //firebase에서 생성한 컬렉션 이름과 같음
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("RegisterActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "문서 추가 오류", e)
            }
    }

    private fun navigateToMainActivity(){
        val intent = Intent(this, MainScreen::class.java)
        startActivity(intent)
        finish() //현재 액티비티를 종료하여 뒤로가기 버튼으로 다시 돌아오지 않도록
    }
}