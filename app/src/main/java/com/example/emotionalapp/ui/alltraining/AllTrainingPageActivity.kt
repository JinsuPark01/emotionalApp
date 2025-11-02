package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AllTrainingAdapter
import com.example.emotionalapp.data.TrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.databinding.ActivityAllTrainingBinding
import com.example.emotionalapp.ui.open.BottomNavActivity

class AllTrainingPageActivity : BottomNavActivity() {

    private lateinit var binding: ActivityAllTrainingBinding
    private lateinit var trainingAdapter: AllTrainingAdapter

    override val isAllTrainingPage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupRecyclerView()
        loadTrainingData()   // 🔹 바로 로드 (날짜 의존 X)
        setupTabListeners()
    }

    private fun setupRecyclerView() {
        trainingAdapter = AllTrainingAdapter(emptyList()) { clickedTrainingItem ->
            // 전시용이므로 잠김 없음
            clickedTrainingItem.targetActivityClass?.let { targetClass ->
                val intent = Intent(this, targetClass).apply {
                    putExtra("TRAINING_ID", clickedTrainingItem.id)
                    putExtra("TRAINING_TITLE", clickedTrainingItem.title)
                }
                startActivity(intent)
            } ?: run {
                Log.e("Navigation", "Target activity class is null for ${clickedTrainingItem.title}")
            }
        }
        binding.trainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingRecyclerView.adapter = trainingAdapter
    }

    private fun loadTrainingData() {
        // 🔹 전부 오픈된 상태로 고정
        val progressArr = arrayOf("GO", "GO", "GO", "GO", "GO")

        val sampleData = listOf(
            TrainingItem(
                "intro001",
                "INTRO",
                "감정의 세계로 떠나는 첫 걸음",
                TrainingType.INTRO,
                progressArr[0],
                R.color.button_color_intro,
                IntroActivity::class.java
            ),
            TrainingItem(
                "et001",
                "1주차 - 정서인식 훈련",
                "나의 감정을 정확히 알아차리기",
                TrainingType.EMOTION_TRAINING,
                progressArr[1],
                R.color.button_color_emotion,
                EmotionActivity::class.java
            ),
            TrainingItem(
                "bt001",
                "2주차 - 신체자각 훈련",
                "몸이 보내는 신호에 귀 기울이기",
                TrainingType.BODY_TRAINING,
                progressArr[2],
                R.color.button_color_body,
                BodyActivity::class.java
            ),
            TrainingItem(
                "mwt001",
                "3주차 - 인지재구성 훈련",
                "생각의 틀을 바꾸는 연습",
                TrainingType.MIND_WATCHING_TRAINING,
                progressArr[3],
                R.color.button_color_mind,
                MindActivity::class.java
            ),
            TrainingItem(
                "eat001",
                "4주차 - 정서표현 및 행동 훈련",
                "건강하게 감정을 표현하고 행동하기",
                TrainingType.EXPRESSION_ACTION_TRAINING,
                progressArr[4],
                R.color.button_color_expression,
                ExpressionActivity::class.java
            )
        )

        trainingAdapter.updateData(sampleData)
    }

    private fun setupTabListeners() {
        binding.tabAll.setOnClickListener {
            // 전시용: 그냥 현재 페이지
            Log.d("AllTrainingPage", "전체 훈련 탭 클릭됨 (현재 페이지)")
        }

        binding.tabToday.setOnClickListener {
            // 필요 없으면 이 이동도 막아도 됨
            val intent = Intent(this, DailyTrainingPageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
