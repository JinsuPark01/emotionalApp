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
        val mail = findViewById<LinearLayout>(R.id.content_layout1)
        val agentChat = findViewById<LinearLayout>(R.id.content_layout2)

        mail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // 이메일 전용 MIME 타입
                putExtra(Intent.EXTRA_EMAIL, arrayOf("sy_un@naver.com"))
                putExtra(Intent.EXTRA_SUBJECT, "문의 사항")
                putExtra(Intent.EXTRA_TEXT, "여기에 내용을 입력하세요.")
            }

            try {
                startActivity(Intent.createChooser(emailIntent, "이메일 앱을 선택하세요"))
            } catch (e: Exception) {
                Toast.makeText(this, "메일 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
//        call.setOnClickListener {
//            // 전화 연결 (전화 앱으로 이동)
//            val intent = Intent(Intent.ACTION_DIAL)
//            intent.data = Uri.parse("tel:119")
//            startActivity(intent)
//            finish()
//        }
        agentChat.setOnClickListener {
            // 전화 연결 (전화 앱으로 이동)
            Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}