package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.intro.IntroTrainingActivity

class IntroActivity : AppCompatActivity() {

    private lateinit var detailRecyclerView: RecyclerView // 변수명 변경 (일관성 및 명확성)
    private lateinit var detailTrainingAdapter: DetailTrainingAdapter
    private val detailTrainingItems = mutableListOf<DetailTrainingItem>() // 변수명 변경

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_page)

        detailRecyclerView = findViewById(R.id.trainingRecyclerView) // XML에 정의된 ID로 변경

        setupRecyclerView()
        loadDetailTrainingData()
    }

    private fun setupRecyclerView() {
        detailTrainingAdapter = DetailTrainingAdapter(detailTrainingItems) { clickedDetailItem ->
            Log.d(
                "IntroActivity",
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
                    "IntroActivity",
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
     * IntroActivity에 표시될 각 세부 훈련 항목에 대한 데이터를 로드하는 함수
     */
    private fun loadDetailTrainingData() {
        // 실제 데이터는 ViewModel, Repository, DB 등에서 가져옵니다.
        val sampleDetailData = listOf(
            DetailTrainingItem(
                id = "intro_detail_001",
                title = "소개",
                subtitle = "정서와 훈련에 대한 설명",
                TrainingType.INTRO,
                currentProgress = 75,
                backgroundColorResId = R.color.button_color_intro,
                targetActivityClass = IntroTrainingActivity::class.java // 실제 액티비티로 변경
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
}