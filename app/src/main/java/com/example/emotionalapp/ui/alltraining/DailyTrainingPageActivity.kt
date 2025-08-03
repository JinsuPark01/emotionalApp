package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.open.BottomNavActivity

class DailyTrainingPageActivity : BottomNavActivity() {

    override val isAllTrainingPage: Boolean = true // 하단 네비게이션 비활성화 유지

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_training)

        setupBottomNavigation()
        setupTabListeners()
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