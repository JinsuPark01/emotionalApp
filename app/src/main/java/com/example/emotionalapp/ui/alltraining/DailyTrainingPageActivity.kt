package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.google.firebase.auth.FirebaseAuth

class DailyTrainingPageActivity : BottomNavActivity() {

    override val isAllTrainingPage: Boolean = true // 하단 네비게이션 비활성화 유지
    private lateinit var IcLogout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_training)
        IcLogout = findViewById(R.id.ic_logout)

        setupBottomNavigation()
        setupTabListeners()

        // ✅ 로그아웃 아이콘 클릭
        IcLogout.setOnClickListener {
            showLogoutDialog()
        }

    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그아웃")
            .setMessage("정말 로그아웃하시겠어요?")
            .setPositiveButton("로그아웃") { _, _ ->
                FirebaseAuth.getInstance().signOut()

                // 혹시 모를 이중 실행 방지
                IcLogout.isEnabled = false

                // 로그인 화면으로 이동 & 백스택 제거
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        // 전체 훈련 탭 클릭 시 이동
        tabAll.setOnClickListener {
            val intent = Intent(this, AllTrainingPageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        // 금일 훈련 탭은 현재 페이지이므로 클릭 시 아무 동작 안 함
        tabToday.setOnClickListener {
            Log.d("TodayTrainingPage", "금일 훈련 탭 클릭됨 (현재 페이지)")
        }
    }
}