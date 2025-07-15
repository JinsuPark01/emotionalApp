package com.example.emotionalapp.ui.alltraining

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.BodyScanRecordAdapter

class BodyTrainingDetailActivity : AppCompatActivity() {

    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View
    private lateinit var layoutPractice: View
    private lateinit var layoutRecord: View
    private lateinit var practiceContent: TextView
    private lateinit var btnStartPractice: Button
    private lateinit var btnStopPractice: Button
    private lateinit var recordRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_detail)

        // 1) Intent 로부터 ID/제목 받기
        val trainingId    = intent.getStringExtra("TRAINING_ID")    ?: ""
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "훈련"

        // 2) 상단 바 설정
        findViewById<TextView>(R.id.tv_page_title).text = trainingTitle
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // 3) 뷰 바인딩
        tabPractice       = findViewById(R.id.tabPractice)
        tabRecord         = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord   = findViewById(R.id.underlineRecord)
        layoutPractice    = findViewById(R.id.layoutPractice)
        layoutRecord      = findViewById(R.id.layoutRecord)
        practiceContent   = findViewById(R.id.tvPracticeContent)
        btnStartPractice  = findViewById(R.id.btnStartPractice)
        btnStopPractice   = findViewById(R.id.btnStopPractice)
        recordRecycler    = findViewById(R.id.rvRecords)

        // 4) Practice 콘텐츠 분기
        val practiceText = when (trainingId) {
            "bt_detail_002" -> """
                전체 몸 스캔 인식하기

                1. 편안하게 서거나 앉으세요.
                2. 발끝에서 머리 꼭대기까지 천천히 신체 감각을 살펴봅니다.
                3. 각 부위에서 느껴지는 감각을 판단 없이 인식하세요.
            """.trimIndent()

            "bt_detail_003" -> """
                특정 감각 집중하기

                1. 호흡, 심장 박동, 또는 배의 움직임 중 하나를 선택합니다.
                2. 선택한 감각에만 모든 주의를 집중합니다.
                3. 잡념이 떠오르면, ‘생각이 떠올랐구나’ 하고 다시 감각으로 돌아옵니다.
            """.trimIndent()

            "bt_detail_004" -> """
                감정-신체 연결 인식

                “감정은 생각보다 먼저 몸에 나타납니다.”

                1. 최근 경험한 강한 감정을 떠올립니다.
                2. 그 감정이 신체의 어디에서 어떻게 느껴지는지 관찰합니다.
                3. 느껴지는 감각을 판단하지 말고 그대로 받아들입니다.
            """.trimIndent()

            "bt_detail_005" -> """
                기본적인 바디 스캔

                1. 편안한 자세로 눈을 감습니다.
                2. 발끝 → 종아리 → 무릎 → 허벅지 → 골반 → 복부 → 가슴 → 어깨 → 팔 → 손 → 목 → 얼굴 순서로 주의를 이동합니다.
                3. 감각이 없어도 괜찮으니, 느껴지는 대로만 관찰합니다.
            """.trimIndent()

            "bt_detail_006" -> """
                바디 스캔: 감각 변화 알아차리기

                1. 어제와 동일한 순서로 바디 스캔을 진행합니다.
                2. 변화된 감각이 있으면 메모합니다.
                3. 판단 없이 몸의 흐름만 관찰하세요.
            """.trimIndent()

            "bt_detail_007" -> """
                먹기 명상 (오감 알아차리기)

                1. 한 입 분량의 음식을 준비합니다.
                2. 색, 형태 → 질감 → 향 → 맛 순으로 오감을 느껴봅니다.
                3. 감정 변화까지 함께 관찰합니다.
            """.trimIndent()

            "bt_detail_008" -> """
                먹기 명상: 감정-먹기 연결 인식

                1. 오늘 먹고 싶은 음식을 떠올립니다.
                2. 먹기 전 자신의 감정을 관찰합니다.
                3. 한 입 먹은 후 감정이 어떻게 변하는지 알아차립니다.
            """.trimIndent()

            else -> "준비 중인 연습입니다."
        }
        practiceContent.text = practiceText

        // 5) Record 데이터 분기
        recordRecycler.layoutManager = LinearLayoutManager(this)
        val recordList = when (trainingId) {
            "bt_detail_002" -> listOf("2025-06-29: 스캔 1회 완료", "2025-06-30: 스캔 1회 완료")
            "bt_detail_003" -> listOf("2025-06-29: 감각 집중 1회", "2025-06-30: 감각 집중 1회")
            "bt_detail_004" -> listOf("2025-06-29: 분노→가슴 답답함", "2025-06-30: 불안→배 조임")
            "bt_detail_005" -> listOf("2025-06-29: 전신 스캔 완료", "2025-06-30: 감각 변화 관찰")
            "bt_detail_006" -> listOf("2025-06-29: 어깨 긴장↓", "2025-06-30: 목 이완↑")
            "bt_detail_007" -> listOf("2025-06-29: 건포도 명상 1회", "2025-06-30: 초콜릿 명상 1회")
            "bt_detail_008" -> listOf("2025-06-29: 긴장→평온 관찰", "2025-06-30: 불안→이완 관찰")
            else            -> emptyList()
        }
        recordRecycler.adapter = BodyScanRecordAdapter(recordList)

        // 6) 탭 리스너 & 초기 탭
        tabPractice.setOnClickListener { selectTab(true) }
        tabRecord  .setOnClickListener { selectTab(false) }
        selectTab(true)
    }

    private fun selectTab(practice: Boolean) {
        tabPractice.setTextColor(
            resources.getColor(if (practice) R.color.black else R.color.gray, null)
        )
        tabRecord.setTextColor(
            resources.getColor(if (practice) R.color.gray else R.color.black, null)
        )
        underlinePractice.visibility = if (practice) View.VISIBLE else View.GONE
        underlineRecord  .visibility = if (practice) View.GONE    else View.VISIBLE
        layoutPractice.visibility = if (practice) View.VISIBLE else View.GONE
        layoutRecord  .visibility  = if (practice) View.GONE    else View.VISIBLE
    }
}
