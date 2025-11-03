package com.example.emotionalapp.ui.login_signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvUsernameError: TextView
    private lateinit var tvPasswordError: TextView
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnSignup: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        tvUsernameError = findViewById(R.id.tvUsernameError)
        tvPasswordError = findViewById(R.id.tvPasswordError)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignup = findViewById(R.id.btnSignup)

        btnLogin.setOnClickListener {
            if (validateInputs()) {
                val email = etUsername.text.toString().trim()
                val password = etPassword.text.toString().trim()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            goToHome()
                        } else {
                            Toast.makeText(this, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    // ✅ 자동 로그인: 앱 화면이 보여지기 직전에 세션 있는지 확인
    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            goToHome()
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, AllTrainingPageActivity::class.java))
        finish() // 로그인 화면을 스택에서 제거
    }

    private fun validateInputs(): Boolean {
        val email = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        var isValid = true

        // 초기화
        tvUsernameError.visibility = View.GONE
        tvPasswordError.visibility = View.GONE

        if (email.isEmpty()) {
            tvUsernameError.text = "이메일을 입력해주세요"
            tvUsernameError.visibility = View.VISIBLE
            isValid = false
        }

        if (password.isEmpty()) {
            tvPasswordError.text = "비밀번호를 입력해주세요"
            tvPasswordError.visibility = View.VISIBLE
            isValid = false
        }

        return isValid
    }
}
