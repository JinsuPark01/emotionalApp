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

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            // 이전 화면으로 돌아가기
            finish()
        }

        val etFeedback = findViewById<EditText>(R.id.etFeedback1)
        val btnSave    = findViewById<Button>(R.id.btnSaveFeedback)

        btnSave.setOnClickListener {
            val feedback = etFeedback.text.toString().trim()
            if (feedback.isEmpty()) {
                Toast.makeText(this, "소감을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("body_training_records", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("feedback1", feedback)
                .apply()

            Toast.makeText(this, "감상이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
