package com.example.studywithswu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnSignIn : Button
    private lateinit var btnLogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance() //firebase에서 인스턴스를 가져옴

        btnLogin = findViewById(R.id.btnLogin)
        btnSignIn = findViewById(R.id.btnSignIn)

        btnLogin.setOnClickListener{
            loginUser()
        }

        btnSignIn.setOnClickListener {  //버튼을 클릭시 이벤트
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.edtEmail).text.toString()
        val password = findViewById<EditText>(R.id.edtPassword).text.toString()

        // 이메일과 비밀번호로 로그인 시도
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시
                    val user = auth.currentUser
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    // 로그인 후 메인 화면 등으로 이동
                    val intent = Intent(this, MainScreen::class.java)
                    startActivity(intent)
                    finish() // 로그인 후 해당 화면 종료
                } else {
                    // 로그인 실패 시
                    Toast.makeText(this, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}