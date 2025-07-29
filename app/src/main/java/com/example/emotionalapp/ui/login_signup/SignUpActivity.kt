package com.example.emotionalapp.ui.login_signup

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirm: EditText
    private lateinit var btnSignup: MaterialButton

    private lateinit var tvEmailError: TextView
    private lateinit var tvPasswordError: TextView
    private lateinit var tvPasswordConfirmError: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        btnSignup = findViewById(R.id.btnSignup)

        tvEmailError = findViewById(R.id.tvEmailError)
        tvPasswordError = findViewById(R.id.tvPasswordError)
        tvPasswordConfirmError = findViewById(R.id.tvPasswordConfirmError)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarSignup)
            .setNavigationOnClickListener { finish() }

        btnSignup.setOnClickListener {
            clearErrors()

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etPasswordConfirm.text.toString().trim()

            var hasError = false

            if (email.isEmpty()) {
                tvEmailError.text = "이메일을 입력해주세요."
                tvEmailError.visibility = android.view.View.VISIBLE
                hasError = true
            }
            if (password.isEmpty()) {
                tvPasswordError.text = "비밀번호를 입력해주세요."
                tvPasswordError.visibility = android.view.View.VISIBLE
                hasError = true
            }
            if (confirmPassword.isEmpty()) {
                tvPasswordConfirmError.text = "비밀번호 확인을 입력해주세요."
                tvPasswordConfirmError.visibility = android.view.View.VISIBLE
                hasError = true
            }
            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                tvPasswordConfirmError.text = "비밀번호가 일치하지 않습니다."
                tvPasswordConfirmError.visibility = android.view.View.VISIBLE
                hasError = true
            }

            if (hasError) return@setOnClickListener

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = email
                        val signUpDate = Timestamp.now()

                        val userData = hashMapOf(
                            "email" to email,
                            "password" to password, // 실제 서비스에서는 평문 저장 금지
                            "signupDate" to signUpDate
                        )

                        db.collection("user").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                showToast("회원가입 완료!")
                                finish()
                            }
                            .addOnFailureListener {
                                auth.currentUser?.delete()
                                showToast("회원가입 실패: 데이터 저장 오류 발생")
                            }
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            tvEmailError.text = "이미 가입된 이메일입니다."
                            tvEmailError.visibility = android.view.View.VISIBLE
                        } else {
                            showToast(exception?.message ?: "회원가입 실패")
                        }
                    }
                }
        }
    }

    private fun clearErrors() {
        tvEmailError.visibility = android.view.View.GONE
        tvPasswordError.visibility = android.view.View.GONE
        tvPasswordConfirmError.visibility = android.view.View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
