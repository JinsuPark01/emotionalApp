package com.example.emotionalapp.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.android.material.button.MaterialButton

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 툴바 뒤로가기 → 이전(Login) 화면으로
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_signup)
            .setNavigationOnClickListener { finish() }

        // 회원가입 완료 → LoginActivity로 돌아가기
        findViewById<MaterialButton>(R.id.btnSignup).setOnClickListener {
            finish()
        }
    }
}
//회원가입 페이지 확인하려고 만든 액티비티입니다.