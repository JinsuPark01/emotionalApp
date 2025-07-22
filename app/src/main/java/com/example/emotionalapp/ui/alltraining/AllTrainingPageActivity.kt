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

    // 뷰 바인딩으로 전환
    private lateinit var binding: ActivityAllTrainingBinding
    private lateinit var trainingAdapter: AllTrainingAdapter

    override val isAllTrainingPage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 바인딩 설정
        binding = ActivityAllTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupRecyclerView()
        loadTrainingData()
        setupTabListeners()
    }

    private fun setupRecyclerView() {
        // 어댑터 초기화 시, 아이템에 지정된 targetActivityClass로 바로 이동
        trainingAdapter = AllTrainingAdapter(emptyList()) { clickedTrainingItem ->
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
        // 각 아이템에 이동할 Activity 클래스를 직접 지정
        val sampleData = listOf(
            TrainingItem(
                "intro001",
                "INTRO",
                "감정의 세계로 떠나는 첫 걸음",
                TrainingType.INTRO,
                "1/3",
                R.color.button_color_intro,
                IntroActivity::class.java
            ),
            TrainingItem(
                "et001",
                "1주차 - 정서인식 훈련",
                "나의 감정을 정확히 알아차리기",
                TrainingType.EMOTION_TRAINING,
                "100%",
                R.color.button_color_emotion,
                EmotionActivity::class.java
            ),
            TrainingItem(
                "bt001",
                "2주차 - 신체자각 훈련",
                "몸이 보내는 신호에 귀 기울이기",
                TrainingType.BODY_TRAINING,
                "잠김",
                R.color.button_color_body,
                BodyActivity::class.java
            ),
            TrainingItem(
                "mwt001",
                "3주차 - 인지재구성 훈련",
                "생각의 틀을 바꾸는 연습",
                TrainingType.MIND_WATCHING_TRAINING,
                "잠김",
                R.color.button_color_mind,
                MindActivity::class.java
            ),
            // --- 여기가 핵심 수정 부분입니다 ---
            TrainingItem(
                "eat001",
                "4주차 - 정서표현 및 행동 훈련",
                "건강하게 감정을 표현하고 행동하기",
                TrainingType.EXPRESSION_ACTION_TRAINING,
                "잠김",
                R.color.button_color_expression,
                ExpressionActivity::class.java
            )
        )
        trainingAdapter.updateData(sampleData)
    }

    private fun setupTabListeners() {
        // 뷰 바인딩을 사용하여 뷰에 접근
        binding.tabAll.setOnClickListener {
            Log.d("AllTrainingPage", "전체 훈련 탭 클릭됨 (현재 페이지)")
        }

        binding.tabToday.setOnClickListener {
            Log.d("AllTrainingPage", "금일 훈련 탭 클릭됨 - DailyTrainingPageActivity로 이동")
            val intent = Intent(this, DailyTrainingPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
