// BodyTrainingActivity.kt
package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.body.BodyTrainingDetailActivity
import com.example.emotionalapp.ui.body.BodyTrainingIntroActivity
import com.example.emotionalapp.ui.body.BodyReportActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity


class BodyActivity : BottomNavActivity() {

    override val isAllTrainingPage = true
    private lateinit var detailRecyclerView: RecyclerView
    private lateinit var detailAdapter: DetailTrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_training)  // 공통 레이아웃 재사용

        // 타이틀(“2주차 – 신체자각 훈련”) 설정
        findViewById<TextView>(R.id.tv_page_title).text =
            intent.getStringExtra("TRAINING_TITLE") ?: "신체자각 훈련"

        // 뒤로가기
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        setupTabListeners()          // 탭(금일/전체) 리스너
        setupBottomNavigation()      // 하단 네비
        setupRecyclerView()          // RecyclerView 초기화
        loadDetailTrainingData()     // 상세 리스트 데이터 로드
    }

    private fun setupTabListeners() {
        findViewById<TextView>(R.id.tabAll).setOnClickListener { /* 전체: 현재 */ }
        findViewById<TextView>(R.id.tabToday).setOnClickListener {
            startActivity(Intent(this, BodyReportActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerView() {
        detailRecyclerView = findViewById(R.id.trainingRecyclerView)
        detailAdapter = DetailTrainingAdapter(emptyList()) { item ->
            // 클릭 시 세부 액티비티가 있다면 이동
            item.targetActivityClass?.let {
                startActivity(Intent(this, it)
                    .putExtra("TRAINING_ID", item.id)
                    .putExtra("TRAINING_TITLE", item.title))
            }
        }
        detailRecyclerView.layoutManager = LinearLayoutManager(this)
        detailRecyclerView.adapter = detailAdapter
    }

    private fun loadDetailTrainingData() {
        val data = listOf(
            DetailTrainingItem("weekly_training", "주차별 점검", "질문지를 통한 마음 돌아보기",
                TrainingType.BODY_TRAINING, "1", "1", "100", R.color.button_color_body, targetActivityClass = WeeklyActivity::class.java),

            DetailTrainingItem("bt_detail_001", "소개", "신체자각 훈련에 대한 설명",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingIntroActivity::class.java),

            DetailTrainingItem("bt_detail_002", "전체 몸 스캔 인식하기", "정서와 관련된 신체 감각 찾기",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),

            DetailTrainingItem("bt_detail_003", "먹기 명상", "음식의 오감 알아차리기",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),

            DetailTrainingItem("bt_detail_004", "감정-신체 연결 인식", "특별한 경험을 기록하기",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),

            DetailTrainingItem("bt_detail_005", "특정 감각 집중하기", "특별한 감각 집중하기",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),

            DetailTrainingItem("bt_detail_006", "바디 스캔", "감각 알아차리기",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),

            DetailTrainingItem("bt_detail_007", "바디 스캔", "미세한 감각 변화 알아차리기",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),

            DetailTrainingItem("bt_detail_008", "먹기 명상", "먹기명상을 통한 감정과 신체 연결 알아차림",
                TrainingType.BODY_TRAINING, "1", "1","100", R.color.button_color_body, targetActivityClass = BodyTrainingDetailActivity::class.java),
        )
        detailAdapter.updateData(data)
    }

}
