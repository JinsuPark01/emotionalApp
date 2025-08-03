package com.example.emotionalapp.ui.body

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.util.setSingleListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.text.SimpleDateFormat
import nl.dionsegijn.konfetti.core.*
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class BodyTrainingRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice_record)

        val konfettiView = findViewById<nl.dionsegijn.konfetti.xml.KonfettiView>(R.id.konfettiView)
        konfettiView.visibility = View.VISIBLE

        konfettiView.start(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 1, TimeUnit.SECONDS).max(100),
                position = Position.Relative(0.5, 0.3)
            )
        )

        AlertDialog.Builder(this)
            .setTitle("훈련 완료")
            .setMessage("잘하셨습니다! 오늘도 당신은 당신의 몸에 주의를 기울였습니다!")
            .setPositiveButton("확인", null)
            .show()

        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ ->
                    finish()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

        val trainingId = intent.getStringExtra("TRAINING_ID") ?: return

        val etFeedback = findViewById<EditText>(R.id.etFeedback1)
        val btnSave = findViewById<Button>(R.id.btnSaveFeedback)



        btnSave.setSingleListener {
            val feedbackText = etFeedback.text.toString().trim()
            if (feedbackText.isEmpty()) {
                Toast.makeText(this, "소감을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setSingleListener
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
                val docId = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }.format(nowDate)

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
