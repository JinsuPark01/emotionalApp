package com.example.emotionalapp.ui.open

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.ui.chat.ChatActivity

open class BottomNavActivity : AppCompatActivity() {

    // 현재 액티비티가 어떤 페이지인지 나타내는 플래그들
    open val isAllTrainingPage: Boolean = false
    open val isChatPage: Boolean = false

    protected fun setupBottomNavigation() {
        // 버튼 View ID 참조
        val btnDashboard = findViewById<LinearLayout>(R.id.nav_dashboard)
        val btnMyTraining = findViewById<LinearLayout>(R.id.nav_my_training)
        val btnChat = findViewById<LinearLayout>(R.id.nav_chat)
        val btnSetting = findViewById<LinearLayout>(R.id.nav_setting)

        val tvMyTraining = btnMyTraining.findViewById<TextView>(R.id.tv_my_training)
        val ivMyTraining = btnMyTraining.findViewById<ImageView>(R.id.iv_my_training)

        val tvChat = btnChat.findViewById<TextView>(R.id.tv_chat)
        val ivChat = btnChat.findViewById<ImageView>(R.id.iv_chat)

        // "나의 훈련" 버튼 처리
        if (isAllTrainingPage) {
            tvMyTraining.setTextColor(Color.parseColor("#00897B"))
            ivMyTraining.setColorFilter(Color.parseColor("#00897B"))
        } else {
            btnMyTraining.setOnClickListener {
                startActivity(Intent(this, AllTrainingPageActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        }

        // "상담톡" 버튼 처리
        if (isChatPage) {
            tvChat.setTextColor(Color.parseColor("#00897B"))
            ivChat.setColorFilter(Color.parseColor("#00897B"))
        } else {
            btnChat.setOnClickListener {
                startActivity(Intent(this, ChatActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        }

        // 나머지 버튼은 공통적으로 "미구현" 알람 표시
        val notImplementedListener = View.OnClickListener {
            Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show()
        }

        btnDashboard.setOnClickListener(notImplementedListener)
        btnSetting.setOnClickListener(notImplementedListener)
    }
}