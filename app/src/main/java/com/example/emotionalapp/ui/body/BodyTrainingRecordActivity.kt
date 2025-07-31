package com.example.emotionalapp.ui.body

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.text.SimpleDateFormat

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

            val prefs = getSharedPreferences("body_training_records", Context.MODE_PRIVATE)
            val key = "feedback_$trainingId"
            prefs.edit().putString(key, feedbackText).apply()

            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email

            if (userEmail != null) {
                val db = FirebaseFirestore.getInstance()

                val record = hashMapOf(
                    "content" to feedbackText,
                    "date" to Timestamp.now(),
                    "trainingId" to trainingId
                )

                // ✅ 날짜 기반 문서 ID 생성
                val nowDate = Timestamp.now().toDate()
                val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
                val docId = formatter.format(nowDate)

                db.collection("user")
                    .document(userEmail)
                    .collection("bodyRecord")
                    .document(docId)  // ← 여기!
                    .set(record)
                    .addOnSuccessListener {
                        Toast.makeText(this, "소감이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("Firestore", "데이터 저장 성공")
                        // 저장 성공 시에만 countComplete.trainingId +1
                        db.collection("user")
                            .document(userEmail)
                            .update("countComplete.$trainingId", FieldValue.increment(1))
                            .addOnSuccessListener {
                                Log.d("Firestore", "카운트 증가 성공")
                                val intent = Intent(this, AllTrainingPageActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "카운트 증가 실패", e)
                            }
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
