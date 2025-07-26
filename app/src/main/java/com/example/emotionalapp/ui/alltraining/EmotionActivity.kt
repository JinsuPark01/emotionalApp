package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.emotion.AnchorActivity
import com.example.emotionalapp.ui.emotion.ArcActivity
import com.example.emotionalapp.ui.emotion.SelectActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity

class EmotionActivity : BottomNavActivity() {

    private lateinit var detailRecyclerView: RecyclerView // 변수명 변경 (일관성 및 명확성)
    private lateinit var detailTrainingAdapter: DetailTrainingAdapter
    private val detailTrainingItems = mutableListOf<DetailTrainingItem>() // 변수명 변경

    override val isAllTrainingPage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_training)

        detailRecyclerView = findViewById(R.id.trainingRecyclerView) // XML에 정의된 ID로 변경

        // 👇 btnBack 처리 추가
        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        setupTabListeners()
        setupBottomNavigation()
        setupRecyclerView()
        loadDetailTrainingData()
    }

    private fun setupRecyclerView() {
        detailTrainingAdapter = DetailTrainingAdapter(detailTrainingItems) { clickedDetailItem ->
            Log.d(
                "EmotionTrainingActivity",
                "Clicked: ${clickedDetailItem.title}, Target: ${clickedDetailItem.targetActivityClass?.simpleName}"
            )

            if (clickedDetailItem.targetActivityClass != null) {
                val intent = Intent(this, clickedDetailItem.targetActivityClass).apply {
                    // 공통적으로 ID와 제목을 넘김
                    putExtra("TRAINING_ID", clickedDetailItem.id)
                    putExtra("TRAINING_TITLE", clickedDetailItem.title)
                }
                startActivity(intent)
            } else {
                Log.w(
                    "EmotionTrainingActivity",
                    "No target activity defined for ${clickedDetailItem.title}"
                )
                Toast.makeText(
                    this,
                    "${clickedDetailItem.title}: 상세 페이지 준비 중입니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        detailRecyclerView.layoutManager = LinearLayoutManager(this)
        detailRecyclerView.adapter = detailTrainingAdapter
    }

    /**
     * EmotionTrainingActivity에 표시될 각 세부 훈련 항목에 대한 데이터를 로드하는 함수
     */
    private fun loadDetailTrainingData() {
        // 실제 데이터는 ViewModel, Repository, DB 등에서 가져옵니다.
        val sampleDetailData = listOf(
            DetailTrainingItem(
                id = "emotion_detail_000",
                title = "주차별 점검",
                subtitle = "질문지를 통한 마음 돌아보기",
                TrainingType.EMOTION_TRAINING,
                currentProgress = "75",
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = WeeklyActivity::class.java // 실제 액티비티로 변경
            ),
            DetailTrainingItem(
                id = "emotion_detail_001",
                title = "정서 선택하기",
                subtitle = "정서와 관련된 신체 감각 찾기",
                TrainingType.EMOTION_TRAINING,
                currentProgress = "75",
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = SelectActivity::class.java // 실제 액티비티로 변경
            ),
            DetailTrainingItem(
                id = "emotion_detail_002",
                title = "현재에 닻 내리기",
                subtitle = "특별한 경험을 기록하기",
                TrainingType.EMOTION_TRAINING,
                currentProgress = "75",
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = AnchorActivity::class.java // 실제 액티비티로 변경
            ),
            DetailTrainingItem(
                id = "emotion_detail_003",
                title = "ARC 정서 경험 기록",
                subtitle = "특별한 경험을 기록하기",
                TrainingType.EMOTION_TRAINING,
                currentProgress = "75",
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = ArcActivity::class.java // 실제 액티비티로 변경
            )
        )
        detailTrainingItems.clear()
        detailTrainingItems.addAll(sampleDetailData)

        // 어댑터에 데이터 변경 알림
        // 만약 어댑터가 이미 생성된 후에 이 함수가 호출된다면 아래 라인이 필요
        if (::detailTrainingAdapter.isInitialized) {
            detailTrainingAdapter.updateData(detailTrainingItems)
        }
    }

    private fun setupTabListeners() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabToday = findViewById<TextView>(R.id.tabToday)

        // 현재 페이지: 전체 훈련이므로 클릭 시 아무 동작 없음
        tabAll.setOnClickListener {
            Log.d("AllTrainingPage", "전체 훈련 탭 클릭됨 (현재 페이지)")
            // 필요하면 데이터 새로고침 추가 가능
        }

        tabToday.setOnClickListener {
            Log.d("AllTrainingPage", "금일 훈련 탭 클릭됨 - TodayTrainingPageActivity로 이동")
            val intent = Intent(this, DailyTrainingPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}