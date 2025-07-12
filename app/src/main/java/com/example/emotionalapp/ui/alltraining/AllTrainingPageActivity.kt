package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView // TextView 임포트 추가
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AllTrainingAdapter
import com.example.emotionalapp.data.TrainingItem
import com.example.emotionalapp.data.TrainingType
import kotlin.collections.addAll
import kotlin.text.clear

// 대상 액티비티들을 임포트 (실제 프로젝트의 액티비티로 대체)
// import com.example.emotionalapp.ui.representative.RepresentativeTrainingActivity
// import com.example.emotionalapp.ui.emotionavoidance.EmotionAvoidanceActivity
// import com.example.emotionalapp.ui.mindwatching.MindWatchingActivity
// import com.example.emotionalapp.ui.detail.GenericTrainingDetailActivity

class AllTrainingPageActivity : AppCompatActivity() {

    private lateinit var trainingRecyclerView: RecyclerView
    private lateinit var trainingAdapter: AllTrainingAdapter
    private val trainingItems = mutableListOf<TrainingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_training_page) // 이 XML에 trainingRecyclerView가 있어야 함

        trainingRecyclerView =
            findViewById(R.id.trainingRecyclerView) // activity_all_training_page.xml 내부의 ID

        setupRecyclerView()
        loadTrainingData() // 데이터 로드

        // 탭 및 하단 네비게이션 리스너 설정 (기존 코드 유지 또는 수정)
        // ... (이전 답변의 탭 리스너 설정 부분 참고) ...
        setupTabListeners() // 탭 리스너 설정 함수 호출
    }

    private fun setupRecyclerView() {
        trainingAdapter = AllTrainingAdapter(trainingItems) { clickedTrainingItem ->
            Log.d(
                "AllTrainingPage",
                "Clicked: ${clickedTrainingItem.title}, Type: ${clickedTrainingItem.trainingType}"
            )

            val intent = Intent().apply {
                // 공통적으로 ID를 넘길 수 있습니다.
                putExtra("TRAINING_ID", clickedTrainingItem.id)
                putExtra("TRAINING_TITLE", clickedTrainingItem.title) // 필요하다면 제목도 전달

                // trainingType에 따라 다른 액티비티로 이동
                when (clickedTrainingItem.trainingType) {
                    TrainingType.INTRO -> {
                        Log.i("Navigation", "INTRO 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        intent.setClass(this@AllTrainingPageActivity, IntroActivity::class.java)
                    }
                    TrainingType.EMOTION_TRAINING -> {
                        Log.i("Navigation", "EMOTION_TRAINING 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        intent.setClass(this@AllTrainingPageActivity, EmotionTrainingActivity::class.java)
                    }
                    TrainingType.BODY_TRAINING -> {
                        Log.i("Navigation", "BODY_TRAINING 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        intent.setClass(this@AllTrainingPageActivity, BodyTrainingActivity::class.java)
                    }
                    TrainingType.MIND_WATCHING_TRAINING -> {
                        Log.i("Navigation", "MIND_WATCHING_TRAINING 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        intent.setClass(this@AllTrainingPageActivity, MindWatchingActivity::class.java)
                    }
                    TrainingType.EXPRESSION_ACTION_TRAINING -> {
                        Log.i("Navigation", "EXPRESSION_ACTION_TRAINING 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        // ExpressionActionActivity가 실제 존재하고 import 되었다면 아래와 같이 변경
                        intent.setClass(this@AllTrainingPageActivity, ExpressionActionActivity::class.java)
                    }
                }
            }
            // intent.component가 설정되었는지 확인 후 startActivity 호출
            if (intent.component != null) {
                startActivity(intent)
            } else {
                Log.e(
                    "Navigation",
                    "Intent component is null. Cannot start activity for ${clickedTrainingItem.title}"
                )
            }
        }
        trainingRecyclerView.layoutManager = LinearLayoutManager(this)
        trainingRecyclerView.adapter = trainingAdapter
    }

    /**
     * 각 훈련 항목에 대한 데이터를 로드하는 함수
     */
    private fun loadTrainingData() {
        // TrainingType 또는 targetActivityClass를 포함하여 데이터 생성
        val sampleData = listOf(
            TrainingItem(
                "intro001",
                "INTRO",
                "감정의 세계로 떠나는 첫 걸음",
                TrainingType.INTRO,
                R.color.button_color_intro
            ),
            TrainingItem(
                "et001",
                "1주차 - 정서인식 훈련",
                "나의 감정을 정확히 알아차리기",
                TrainingType.EMOTION_TRAINING,
                R.color.button_color_emotion
            ),
            TrainingItem(
                "bt001",
                "2주차 - 신체자각 훈련",
                "몸이 보내는 신호에 귀 기울이기",
                TrainingType.BODY_TRAINING,
                R.color.button_color_body
            ),
            TrainingItem(
                "mwt001",
                "3주차 - 인지재구성 훈련",
                "생각의 틀을 바꾸는 연습",
                TrainingType.MIND_WATCHING_TRAINING,
                R.color.button_color_mind
            ),
            TrainingItem(
                "eat001",
                "4주차 - 정서표현 및 행동 훈련",
                "건강하게 감정을 표현하고 행동하기",
                TrainingType.EXPRESSION_ACTION_TRAINING,
                R.color.button_color_expression
            )
            // 필요하다면 targetActivityClass를 직접 지정하는 아이템도 추가 가능
            // TrainingItem("custom001", "커스텀 액티비티로", "설명", TrainingType.DEFAULT_DETAIL, CustomActivity::class.java)
        )
        trainingItems.clear()
        trainingItems.addAll(sampleData)
        trainingAdapter.updateData(trainingItems) // 어댑터에 새로 만든 updateData 함수 사용
    }

    private fun setupTabListeners() {
        // activity_all_training_page.xml의 TextView ID에 따라 수정
        val tabAll = findViewById<TextView>(R.id.tabAll) // 예시 ID
        val tabToday = findViewById<TextView>(R.id.tabToday) // 예시 ID

        tabAll.setOnClickListener {
            Log.d("AllTrainingPage", "전체 훈련 탭 클릭됨 (현재 페이지)")
            // 필요하다면 데이터 새로고침 등
            // loadTrainingData()
        }

        tabToday.setOnClickListener {
            Log.d("AllTrainingPage", "금일 훈련 탭 클릭됨 - 다른 액티비티로 이동 구현 필요")
            // 예: val intent = Intent(this, TodayTrainingActivity::class.java)
            // startActivity(intent)
            // finish() // 현재 액티비티를 닫고 새 액티비티로만 이동할 경우
        }

        // 하단 네비게이션 리스너 설정도 여기에 추가
        // 예: val bottomHome = findViewById<View>(R.id.bottom_nav_home_id) // bottom_nav.xml 내의 ID
        // bottomHome.setOnClickListener { /* 홈으로 이동 로직 */ }
    }
}