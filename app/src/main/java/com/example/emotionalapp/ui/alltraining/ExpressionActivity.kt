package com.example.emotionalapp.ui.alltraining

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.expression.*
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ExpressionActivity : BottomNavActivity() {

    private lateinit var adapter: DetailTrainingAdapter

    override val isAllTrainingPage: Boolean = true

    private var userDiffDays: Long = 0
    private var countCompleteMap: Map<String, Long> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_training)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupTabListeners()
        setupBottomNavigation()
        calculateDiffDaysAndGetCount { loadTrainingData() }

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

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.trainingRecyclerView)
        adapter = DetailTrainingAdapter(emptyList()) { item ->

            // "잠김"일 경우 클릭 무시 또는 토스트 메시지 출력
            if (item.currentProgress == "잠김") {
                Toast.makeText(this, "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            } else if (item.progressDenominator == item.progressNumerator && item.currentProgress != "GO") {
                Toast.makeText(this, "모두 완료한 훈련입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            }

            item.targetActivityClass?.let { targetClass ->
                val intent = Intent(this, targetClass)
                startActivity(intent)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
        if (userDiffDays in 22L..28L && (countCompleteMap["weekly"] ?: 0) <= 3) {
            AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("주차별 점검을 먼저 시행하세요!")
                .setPositiveButton("확인", null)
                .setCancelable(false)
                .show()
        }

        val denominatorArr: Array<String> = when (userDiffDays) {
            22L -> arrayOf("4", "잠김", "잠김", "잠김", "잠김", "잠김")
            23L -> arrayOf("4", "1", "1", "잠김", "잠김", "잠김")
            24L -> arrayOf("4", "2", "2", "잠김", "잠김", "잠김")
            25L -> arrayOf("4", "2", "2", "GO", "잠김", "잠김")
            26L -> arrayOf("4", "2", "2", "GO", "1", "1")
            27L -> arrayOf("4", "2", "2", "GO", "2", "2")
            28L -> arrayOf("4", "2", "2", "GO", "3", "3")
            else -> arrayOf("잠김", "잠김", "잠김", "잠김", "잠김", "잠김")
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
        fun getCurrentProgressFor4(denominator: String): String {
            return if (denominator == "잠김") {
                "잠김"
            } else {
                "GO"
            }
        }

        val trainingList = listOf(
            DetailTrainingItem(
                id = "weekly_training",
                title = "주차별 점검",
                subtitle = "질문지를 통한 마음 돌아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                progressDenominator = denominatorArr[0],
                currentProgress = getCurrentProgress("weekly", denominatorArr[0]),
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = WeeklyActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서회피 교육",
                subtitle = "정서 회피에 대해 알아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = "1",
                progressDenominator = "1",
                currentProgress = "GO",
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = AvoidanceGuideActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "회피 일지 작성하기",
                subtitle = "나의 회피 습관을 기록하고 관찰하기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = countCompleteMap["avoidance"]?.toString() ?: "0",
                progressDenominator = denominatorArr[1],
                currentProgress = getCurrentProgress("avoidance", denominatorArr[1]),
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = AvoidanceActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서 머무르기",
                subtitle = "감정을 피하지 않고 느껴보는 연습",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = countCompleteMap["stay"]?.toString() ?: "0",
                progressDenominator = denominatorArr[2],
                currentProgress = getCurrentProgress("stay", denominatorArr[2]),
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = StayActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "정서-주도 행동 교육",
                subtitle = "정서-주도 행동에 대해 알아보기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = "1",
                progressDenominator = "1",
                currentProgress = getCurrentProgressFor4(denominatorArr[3]),
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = DrivenActionGuideActivity::class.java
            ),
            DetailTrainingItem(
                id = "avoidance_training",
                title = "반대 행동 하기",
                subtitle = "감정과 반대로 행동하는 연습",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = countCompleteMap["opposite"]?.toString() ?: "0",
                progressDenominator = denominatorArr[4],
                currentProgress = getCurrentProgress("opposite", denominatorArr[4]),
                backgroundColorResId = R.color.purple_700,
                targetActivityClass = OppositeActionActivity::class.java
            ),
            DetailTrainingItem(
                id = "driven_action_training",
                title = "대안 행동 찾기",
                subtitle = "감정을 다루는 다른 방법 찾기",
                trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                progressNumerator = countCompleteMap["alternative"]?.toString() ?: "0",
                progressDenominator = denominatorArr[5],
                currentProgress = getCurrentProgress("alternative", denominatorArr[5]),
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
