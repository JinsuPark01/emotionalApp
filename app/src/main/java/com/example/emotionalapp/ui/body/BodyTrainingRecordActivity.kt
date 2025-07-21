package com.example.emotionalapp.ui.body

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

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
        val btnSave    = findViewById<Button>(R.id.btnSaveFeedback)

        // 3) 저장 버튼 클릭
        btnSave.setOnClickListener {
            val feedbackText = etFeedback.text.toString().trim()
            if (feedbackText.isEmpty()) {
                Toast.makeText(this, "소감을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4) SharedPreferences에 TrainingId별 키로 저장
            val prefs = getSharedPreferences("body_training_records", Context.MODE_PRIVATE)
            val key = "feedback_$trainingId"
            prefs.edit()
                .putString(key, feedbackText)
                .apply()  // feedback_bt_detail_002 형태로 저장

            // TODO: 서버/DB 연동 로직 추가

            Toast.makeText(this, "소감이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
