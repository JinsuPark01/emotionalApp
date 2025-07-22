package com.example.emotionalapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그인 버튼 클릭 → 전체 훈련 화면
        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, AllTrainingPageActivity::class.java))
            finish()
        }

        // 회원가입 버튼 클릭 → SignUpActivity
        findViewById<MaterialButton>(R.id.btnSignup).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}

//로그인 페이지 확인하려고 만든 액티비티입니다.