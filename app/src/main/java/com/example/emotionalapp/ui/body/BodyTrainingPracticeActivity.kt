package com.example.emotionalapp.ui.body

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

class BodyTrainingPracticeActivity : AppCompatActivity() {

    // ‘멈춤’ 버튼을 나중에 사용하기 위해 멤버 변수로 선언
    private lateinit var btnStopPractice: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice)

        // 1) 뒤로가기 버튼
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // 2) Intent로부터 ID/제목 받기
        val trainingId    = intent.getStringExtra("TRAINING_ID") ?: ""
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "연습"

        findViewById<TextView>(R.id.tv_practice_title).text = trainingTitle

        // ID에 따라 서로 다른 연습 내용을 분기
        val detailText = when (trainingId) {
            "bt_detail_002" -> "DAY 1 연습"
            "bt_detail_003" -> "DAY 2 연습"
            "bt_detail_004" -> "DAY 3 연습"
            "bt_detail_005" -> "DAY 4 연습"
            "bt_detail_006" -> "DAY 5 연습"
            "bt_detail_007" -> "DAY 6 연습"
            "bt_detail_008" -> "DAY 7 연습"
            else -> "준비 중인 연습입니다."
        }

        findViewById<TextView>(R.id.tv_practice_detail).text = detailText

        // 4) 버튼 바인딩
        val btnStart  = findViewById<Button>(R.id.btnStart)
        btnStopPractice = findViewById<Button>(R.id.btnStopPractice)
        val btnRecord = findViewById<Button>(R.id.btnRecord)

        // 5) 클릭 리스너 등록
        btnStart.setOnClickListener {
            Toast.makeText(this, "연습을 시작합니다.", Toast.LENGTH_SHORT).show()
            // TODO: 실제 타이머 시작 로직 추가
        }
        btnStopPractice.setOnClickListener {
            Toast.makeText(this, "연습을 중단했습니다.", Toast.LENGTH_SHORT).show()
            // TODO: 타이머 중단 로직 추가
        }
        btnRecord.setOnClickListener {
            Toast.makeText(this, "연습 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            Intent(this, BodyTrainingRecordActivity::class.java).apply {
                putExtra("TRAINING_ID", trainingId)
            }.also { startActivity(it) }
        }
    }
}