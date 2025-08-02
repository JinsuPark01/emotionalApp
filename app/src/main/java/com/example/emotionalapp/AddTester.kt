package com.example.emotionalapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddTester : AppCompatActivity() {

    private lateinit var btnGenerateDummyUsers: Button
    private lateinit var btnDeleteDummyUsers: Button

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_signup)

        btnGenerateDummyUsers = findViewById(R.id.btnGenerateDummyUsers)
        btnDeleteDummyUsers = findViewById(R.id.btnDeleteDummyUsers)


        btnGenerateDummyUsers.setOnClickListener {
            generateDummyUserAt(0)
        }

        btnDeleteDummyUsers.setOnClickListener {
            deleteDummyUserRecursive(0)
        }
    }

    private fun generateDummyUserAt(index: Int) {
        val total = 28
        if (index >= total) {
            Toast.makeText(this, "더미 회원 생성 완료", Toast.LENGTH_SHORT).show()
            return
        }

        val email = "day${index + 1}@naver.com"
        val password = "123456"

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -index)
        val timestamp = Timestamp(calendar.time)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userData = hashMapOf(
                        "email" to email,
                        "password" to password, // 실제 서비스에서는 저장 ❌
                        "signupDate" to timestamp,
                        "countComplete" to hashMapOf(
                            "weekly" to 0,
                            "select" to 0,
                            "anchor" to 0,
                            "arc" to 0,
                            "art" to 0,
                            "trap" to 0,
                            "auto" to 0,
                            "bt_detail_002" to 0,
                            "bt_detail_003" to 0,
                            "bt_detail_004" to 0,
                            "bt_detail_005" to 0,
                            "bt_detail_006" to 0,
                            "bt_detail_007" to 0,
                            "bt_detail_008" to 0,
                            "alternative" to 0,
                            "avoidance" to 0,
                            "opposite" to 0,
                            "stay" to 0
                        )
                    )

                    db.collection("user").document(email)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d("DummySignUp", "등록 완료: $email")
                            generateDummyUserAt(index + 1) // 다음 유저 생성
                        }
                        .addOnFailureListener {
                            Log.e("DummySignUp", "Firestore 저장 실패: $email", it)
                            generateDummyUserAt(index + 1) // 실패해도 계속 진행
                        }

                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        Log.w("DummySignUp", "이미 등록된 이메일: $email")
                    } else {
                        Log.e("DummySignUp", "회원가입 실패: $email", exception)
                    }
                    generateDummyUserAt(index + 1) // 다음 유저로 계속 진행
                }
            }
    }
    private fun deleteDummyUserRecursive(index: Int) {
        if (index >= 28) {
            Toast.makeText(this, "모든 더미 유저 삭제 완료", Toast.LENGTH_SHORT).show()
            return
        }

        val email = "day${index + 1}@naver.com"
        db.collection("user").document(email)
            .delete()
            .addOnSuccessListener {
                Log.d("DummyDelete", "삭제 완료: $email")
                deleteDummyUserRecursive(index + 1)  // 다음 사용자 삭제 호출
            }
            .addOnFailureListener { e ->
                Log.e("DummyDelete", "삭제 실패: $email", e)
                // 실패했어도 다음으로 넘어가려면 아래 호출 유지
                deleteDummyUserRecursive(index + 1)
            }
    }
}
