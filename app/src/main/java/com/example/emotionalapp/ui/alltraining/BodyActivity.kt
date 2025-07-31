// BodyTrainingActivity.kt
package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
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
import com.example.emotionalapp.ui.body.BodyTrainingDetailActivity
import com.example.emotionalapp.ui.body.BodyTrainingIntroActivity
import com.example.emotionalapp.ui.body.BodyReportActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class BodyActivity : BottomNavActivity() {

    override val isAllTrainingPage = true
    private lateinit var detailRecyclerView: RecyclerView
    private lateinit var detailAdapter: DetailTrainingAdapter

    private var userDiffDays: Long = 0
    private var countCompleteMap: Map<String, Long> = emptyMap()

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
        calculateDiffDaysAndGetCount { loadDetailTrainingData() }     // 상세 리스트 데이터 로드
    }

    private fun calculateDiffDaysAndGetCount(onFinished: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (userEmail != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("user").document(userEmail).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.contains("signupDate")) {
                            val timestamp = document.getTimestamp("signupDate")
                            if (timestamp != null) {
                                val koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul")
                                val dateFormat =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).apply {
                                        timeZone = koreaTimeZone
                                    }

                                val joinDateStr = dateFormat.format(timestamp.toDate())
                                val todayStr = dateFormat.format(Date())

                                val joinDate = dateFormat.parse(joinDateStr)
                                val todayDate = dateFormat.parse(todayStr)

                                if (joinDate != null && todayDate != null) {
                                    val diffMillis = todayDate.time - joinDate.time
                                    userDiffDays = TimeUnit.MILLISECONDS.toDays(diffMillis) + 1
                                    Log.d("UserJoinDate", "가입 후 ${userDiffDays}일차 (한국 시간 기준)")
                                }
                            }
                        }
                        if (document.contains("countComplete")) {
                            val rawMap = document["countComplete"] as? Map<*, *>
                            if (rawMap != null) {
                                countCompleteMap = rawMap.mapNotNull { (key, value) ->
                                    val k = key as? String
                                    val v = when (value) {
                                        is Long -> value
                                        is Number -> value.toLong()
                                        else -> null
                                    }
                                    if (k != null && v != null) k to v else null
                                }.toMap()
                                Log.d("CountComplete", "가져온 맵: $countCompleteMap")
                            } else {
                                Log.w("CountComplete", "countComplete 맵이 null이거나 형식이 다름")
                            }
                        }
                    } else {
                        Log.w("UserJoinDateInATPA", "signupDate 필드가 없음")
                    }

                    // ✅ 여기서 호출
                    onFinished()
                }
                .addOnFailureListener { e ->
                    Log.e("UserJoinDate", "Firestore 에러: ${e.message}")
                    onFinished() // 실패해도 계속 진행할지 여부는 판단 필요
                }
        } else {
            onFinished() // 유저 정보 없음
        }
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

            // "잠김"일 경우 클릭 무시 또는 토스트 메시지 출력
            if (item.currentProgress == "잠김") {
                Toast.makeText(this, "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            } else if (item.progressDenominator == item.progressNumerator && item.currentProgress != "GO") {
                Toast.makeText(this, "모두 완료한 훈련입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            }

            // 클릭 시 세부 액티비티가 있다면 이동
            item.targetActivityClass?.let {
                startActivity(
                    Intent(this, it)
                        .putExtra("TRAINING_ID", item.id)
                        .putExtra("TRAINING_TITLE", item.title)
                )
            }
        }
        detailRecyclerView.layoutManager = LinearLayoutManager(this)
        detailRecyclerView.adapter = detailAdapter
    }

    private fun loadDetailTrainingData() {
        val denominatorArr: Array<String> = when (userDiffDays) {
            8L -> arrayOf("2", "1", "잠김", "잠김", "잠김", "잠김", "잠김", "잠김")
            9L -> arrayOf("2", "1", "1", "잠김", "잠김", "잠김", "잠김", "잠김")
            10L -> arrayOf("2", "1", "1", "1", "잠김", "잠김", "잠김", "잠김")
            11L -> arrayOf("2", "1", "1", "1", "1", "잠김", "잠김", "잠김")
            12L -> arrayOf("2", "1", "1", "1", "1", "1", "잠김", "잠김")
            13L -> arrayOf("2", "1", "1", "1", "1", "1", "1", "잠김")
            14L -> arrayOf("2", "1", "1", "1", "1", "1", "1", "1")
            else -> arrayOf("잠김", "잠김", "잠김", "잠김", "잠김", "잠김", "잠김", "잠김")
        }

        // 헬퍼 함수: 분모가 잠김이면 currentProgress를 "잠김"으로 반환
        fun getCurrentProgress(key: String, denominator: String): String {
            return if (denominator == "잠김") {
                "잠김"
            } else {
                val numerator = countCompleteMap[key]?.toString() ?: "0"
                "$numerator/$denominator"
            }
        }

        val data = listOf(
            DetailTrainingItem(
                "weekly_training", "주차별 점검", "질문지를 통한 마음 돌아보기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                progressDenominator = denominatorArr[0],
                currentProgress = getCurrentProgress("weekly", denominatorArr[0]),
                R.color.button_color_body, targetActivityClass = WeeklyActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_001",
                "소개",
                "신체자각 훈련에 대한 설명",
                TrainingType.BODY_TRAINING,
                progressNumerator = "1",
                progressDenominator = "1",
                currentProgress = "GO",
                R.color.button_color_body,
                targetActivityClass = BodyTrainingIntroActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_002",
                "전체 몸 스캔 인식하기",
                "정서와 관련된 신체 감각 찾기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_002"]?.toString() ?: "0",
                progressDenominator = denominatorArr[1],
                currentProgress = getCurrentProgress("bt_detail_002", denominatorArr[1]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_003",
                "먹기 명상",
                "음식의 오감 알아차리기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_003"]?.toString() ?: "0",
                progressDenominator = denominatorArr[2],
                currentProgress = getCurrentProgress("bt_detail_003", denominatorArr[2]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_004",
                "감정-신체 연결 인식",
                "특별한 경험을 기록하기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_004"]?.toString() ?: "0",
                progressDenominator = denominatorArr[3],
                currentProgress = getCurrentProgress("bt_detail_004", denominatorArr[3]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_005",
                "특정 감각 집중하기",
                "특별한 감각 집중하기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_005"]?.toString() ?: "0",
                progressDenominator = denominatorArr[4],
                currentProgress = getCurrentProgress("bt_detail_005", denominatorArr[4]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_006",
                "바디 스캔",
                "감각 알아차리기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_006"]?.toString() ?: "0",
                progressDenominator = denominatorArr[5],
                currentProgress = getCurrentProgress("bt_detail_006", denominatorArr[5]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_007",
                "바디 스캔",
                "미세한 감각 변화 알아차리기",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_007"]?.toString() ?: "0",
                progressDenominator = denominatorArr[6],
                currentProgress = getCurrentProgress("bt_detail_007", denominatorArr[6]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),

            DetailTrainingItem(
                "bt_detail_008",
                "먹기 명상",
                "먹기명상을 통한 감정과 신체 연결 알아차림",
                TrainingType.BODY_TRAINING,
                progressNumerator = countCompleteMap["bt_detail_008"]?.toString() ?: "0",
                progressDenominator = denominatorArr[7],
                currentProgress = getCurrentProgress("bt_detail_008", denominatorArr[7]),
                R.color.button_color_body,
                targetActivityClass = BodyTrainingDetailActivity::class.java
            ),
        )
        detailAdapter.updateData(data)
    }

}
