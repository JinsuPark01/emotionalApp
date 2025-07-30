package com.example.emotionalapp.ui.body

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BodyTrainingRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice_record)

        // 1) 뒤로가기 버튼
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 2) Intent로부터 trainingId 받기
        val trainingId = intent.getStringExtra("TRAINING_ID") ?: return

        val etFeedback = findViewById<EditText>(R.id.etFeedback1)
        val btnSave = findViewById<Button>(R.id.btnSaveFeedback)

        // 3) 저장 버튼 클릭
        btnSave.setOnClickListener {
            val feedbackText = etFeedback.text.toString().trim()
            if (feedbackText.isEmpty()) {
                Toast.makeText(this, "소감을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SharedPreferences에 저장 (원래 코드 유지)
            val prefs = getSharedPreferences("body_training_records", Context.MODE_PRIVATE)
            val key = "feedback_$trainingId"
            prefs.edit()
                .putString(key, feedbackText)
                .apply()

            // Firestore에 저장 추가
            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email

            if (userEmail != null) {
                val db = FirebaseFirestore.getInstance()
                val record = hashMapOf(
                    "content" to feedbackText,
                    "date" to System.currentTimeMillis()
                )

                db.collection("users")
                    .document(userEmail)
                    .collection("bodyRecord")
                    .add(record)
                    .addOnSuccessListener {
                        Toast.makeText(this, "소감이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "로그인된 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
