package com.example.emotionalapp.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.open.BottomNavActivity

class ChatActivity : BottomNavActivity() {

    override val isChatPage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupBottomNavigation()

        // 카드 뷰 내부의 content_layout 클릭 이벤트 연결
        val call = findViewById<LinearLayout>(R.id.content_layout1)
        val agentChat = findViewById<LinearLayout>(R.id.content_layout2)

        call.setOnClickListener {
            // 전화 연결 (전화 앱으로 이동)
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:119")
            startActivity(intent)
        }
        agentChat.setOnClickListener {
            // 전화 연결 (전화 앱으로 이동)
            Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}