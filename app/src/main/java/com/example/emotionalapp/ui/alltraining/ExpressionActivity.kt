package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.emotion.EmotionReportActivity
import com.example.emotionalapp.ui.expression.*
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity

class ExpressionActivity : BottomNavActivity() {

    private lateinit var adapter: DetailTrainingAdapter

    override val isAllTrainingPage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_training)

        setupRecyclerView()
        loadTrainingData()

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupTabListeners()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.trainingRecyclerView)
        adapter = DetailTrainingAdapter(emptyList()) { item ->
            item.targetActivityClass?.let { targetClass ->
                val intent = Intent(this, targetClass)
                startActivity(intent)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        val trainingList = listOf(
            DetailTrainingItem(
                id = "weekly_training",
                title = "주차별 점검",
                subtitle = "질문지를 통한 마음 돌아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "진행하기",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = WeeklyActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서회피 훈련 교육",
                subtitle = "정서 회피에 대해 알아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "0/3",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = AvoidanceGuideActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "회피 일지 작성하기",
                subtitle = "나의 회피 습관을 기록하고 관찰하기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "0/3",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = AvoidanceChecklistActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서 머무르기",
                subtitle = "감정을 피하지 않고 느껴보는 연습",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "0/3",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = EmotionSelectionActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서-주도 행동 훈련 교육",
                subtitle = "정서-주도 행동에 대해 알아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "0/3",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = DrivenActionGuideActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "반대 행동 하기",
                subtitle = "감정과 반대로 행동하는 연습",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "0/3",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = OppositeActionActivity::class.java
            ),
            DetailTrainingItem(
                id = "driven_action_training",
                title = "대안 행동 찾기",
                subtitle = "감정을 다루는 다른 방법 찾기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                "1", "1", currentProgress = "50",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = AlternativeActionActivity::class.java
            )
        )
        adapter.updateData(trainingList)
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        tabAll.setOnClickListener {
            Log.d("AllTrainingPage", "전체 훈련 탭 클릭됨 (현재 페이지)")
        }

        tabToday.setOnClickListener {
            Log.d("AllTrainingPage", "금일 훈련 탭 클릭됨 - TodayTrainingPageActivity로 이동")
            val intent = Intent(this, ExpressionReportActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
