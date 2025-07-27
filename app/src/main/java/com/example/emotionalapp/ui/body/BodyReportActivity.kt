package com.example.emotionalapp.ui.body

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.ReportAdapter
import com.example.emotionalapp.data.ReportItem
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.ui.alltraining.BodyActivity
import com.example.emotionalapp.ui.alltraining.EmotionActivity
import com.example.emotionalapp.ui.open.BottomNavActivity

class BodyReportActivity : BottomNavActivity() {

    override val isAllTrainingPage: Boolean = true // 하단 네비게이션 비활성화 유지

    private lateinit var trainingRecyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private val reportList = mutableListOf<ReportItem>() // 더미 데이터용 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_report)

        trainingRecyclerView = findViewById(R.id.trainingRecyclerView)

        setupBottomNavigation()
        setupTabListeners()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        trainingRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(reportList) { reportItem ->
            val intent = when (reportItem.name) {
                // 기록 아이템 name에 따라 다른 액티비티로 이동하게끔 구현해놨음
                "PHQ-9 결과 보기" -> Intent(this, AllTrainingPageActivity::class.java)
                "GAD-7 결과 보기" -> Intent(this, AllTrainingPageActivity::class.java)
                "PANAS 결과 보기" -> Intent(this, AllTrainingPageActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }


        trainingRecyclerView.adapter = adapter

        // 예시 데이터 추가
        reportList.add(ReportItem("2025-07-27", "PHQ-9 결과 보기"))
        reportList.add(ReportItem("2025-07-26", "GAD-7 결과 보기"))
        reportList.add(ReportItem("2025-07-25", "PANAS 결과 보기"))

        adapter.notifyDataSetChanged()
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        // 전체 훈련 탭 클릭 시 이동
        tabAll.setOnClickListener {
            val intent = Intent(this, BodyActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 금일 훈련 탭은 현재 페이지이므로 클릭 시 아무 동작 없음
        tabToday.setOnClickListener {
            Log.d("TodayTrainingPage", "금일 훈련 탭 클릭됨 (현재 페이지)")
        }
    }
}
