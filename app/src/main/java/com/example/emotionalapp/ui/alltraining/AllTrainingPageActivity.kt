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

    // 예시 대상 액티비티 클래스 (실제 액티비티로 대체 필요)
    // 아래 클래스들은 실제 프로젝트에 존재해야 합니다.
    class RepresentativeTrainingActivity : AppCompatActivity() { /* ... */ }
    class EmotionAvoidanceActivity : AppCompatActivity() { /* ... */ }
    class MindWatchingActivity : AppCompatActivity() { /* ... */ }
    class GenericTrainingDetailActivity : AppCompatActivity() { /* ... */ }


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
                    TrainingType.REPRESENTATIVE_TRAINING -> {
                        //setClass(this@AllTrainingPageActivity, RepresentativeTrainingActivity::class.java)
                        // 임시로 Log만 출력 (실제 액티비티 연결 필요)
                        Log.i("Navigation", "대표 훈련 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        setClass(
                            this@AllTrainingPageActivity,
                            RepresentativeTrainingActivity::class.java
                        ) // 실제 액티비티 연결
                    }

                    TrainingType.EMOTION_AVOIDANCE_TRAINING -> {
                        //setClass(this@AllTrainingPageActivity, EmotionAvoidanceActivity::class.java)
                        Log.i("Navigation", "정서회피 훈련 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        setClass(this@AllTrainingPageActivity, EmotionAvoidanceActivity::class.java)
                    }

                    TrainingType.MIND_WATCHING_TRAINING -> {
                        //setClass(this@AllTrainingPageActivity, MindWatchingActivity::class.java)
                        Log.i("Navigation", "마음보기 훈련 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        setClass(this@AllTrainingPageActivity, MindWatchingActivity::class.java)
                    }

                    TrainingType.DEFAULT_DETAIL -> {
                        //setClass(this@AllTrainingPageActivity, GenericTrainingDetailActivity::class.java)
                        Log.i("Navigation", "일반 상세 페이지로 이동 준비: ${clickedTrainingItem.title}")
                        setClass(
                            this@AllTrainingPageActivity,
                            GenericTrainingDetailActivity::class.java
                        )
                    }
                    // targetActivityClass를 직접 사용하는 경우 (선택적)
                    // else -> {
                    //    if (clickedTrainingItem.targetActivityClass != null) {
                    //        setClass(this@AllTrainingPageActivity, clickedTrainingItem.targetActivityClass)
                    //    } else {
                    //        Log.e("Navigation", "Target activity class is null for ${clickedTrainingItem.title}")
                    //        return@AllTrainingAdapter // 이동할 대상이 없으면 아무것도 안 함
                    //    }
                    // }
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

    private fun loadTrainingData() {
        // TrainingType 또는 targetActivityClass를 포함하여 데이터 생성
        val sampleData = listOf(
            TrainingItem(
                "rep001",
                "대표 훈련 시작하기",
                "가장 중요한 훈련을 바로 경험해보세요.",
                TrainingType.REPRESENTATIVE_TRAINING
            ),
            TrainingItem(
                "ea001",
                "정서회피 알아보기",
                "정서회피가 무엇인지 이해합니다.",
                TrainingType.EMOTION_AVOIDANCE_TRAINING
            ),
            TrainingItem(
                "mw001",
                "마음보기 첫걸음",
                "내 마음을 관찰하는 연습을 시작합니다.",
                TrainingType.MIND_WATCHING_TRAINING
            ),
            TrainingItem("gen001", "일반 훈련 상세", "이것은 일반 훈련 항목입니다.", TrainingType.DEFAULT_DETAIL)
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