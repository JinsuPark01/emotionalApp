package com.example.emotionalapp.ui.alltraining

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

class BodyTrainingIntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_intro)

        // 상단 타이틀
        findViewById<TextView>(R.id.tv_page_title).text = "소개"
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // (추가 로직 없음: 고정된 설명만 보여줍니다)
    }
}
