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
                            // 로그인 성공 → 다음 액티비티로 이동
                            startActivity(Intent(this, AllTrainingPageActivity::class.java))
                            finish()
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
