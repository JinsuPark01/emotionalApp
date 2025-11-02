package com.example.emotionalapp.ui.alltraining

import android.app.AlertDialog
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
import com.example.emotionalapp.ui.emotion.EmotionReportActivity
import com.example.emotionalapp.ui.emotion.SelectActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class EmotionActivity : BottomNavActivity() {

    private lateinit var detailRecyclerView: RecyclerView // 변수명 변경 (일관성 및 명확성)
    private lateinit var detailTrainingAdapter: DetailTrainingAdapter
    private val detailTrainingItems = mutableListOf<DetailTrainingItem>() // 변수명 변경
    private var userDiffDays: Long = 0
    private var countCompleteMap: Map<String, Long> = emptyMap()

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
        calculateDiffDaysAndGetCount { loadDetailTrainingData() }
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
        detailTrainingAdapter = DetailTrainingAdapter(detailTrainingItems) { clickedDetailItem ->
            Log.d(
                "EmotionTrainingActivity",
                "Clicked: ${clickedDetailItem.title}, Target: ${clickedDetailItem.targetActivityClass?.simpleName}"
            )
            // "잠김"일 경우 클릭 무시 또는 토스트 메시지 출력
            if (clickedDetailItem.currentProgress == "잠김") {
                Toast.makeText(this, "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            } else if (clickedDetailItem.progressDenominator == clickedDetailItem.progressNumerator) {
                Toast.makeText(this, "모두 완료한 훈련입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            }


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
        if (userDiffDays in 1L..7L && (countCompleteMap["weekly"] ?: 0) <= 0) {
            AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("주차별 점검을 먼저 시행하세요!")
                .setPositiveButton("확인", null)
                .setCancelable(false)
                .show()
        }
        //val denominatorArr = arrayOf("4", "14", "4", "4")
        val denominatorArr = arrayOf("99", "99", "99", "99")

        // 헬퍼 함수: 분모가 잠김이면 currentProgress를 "잠김"으로 반환
        fun getCurrentProgress(key: String, denominator: String): String {
            return if (denominator == "잠김") {
                "잠김"
            } else {
                val numerator = countCompleteMap[key]?.toString() ?: "0"
                "$numerator/$denominator"
            }
        }

        val sampleDetailData = listOf(
            DetailTrainingItem(
                id = "weekly_training",
                title = "주차별 점검",
                subtitle = "질문지를 통한 마음 돌아보기",
                trainingType = TrainingType.EMOTION_TRAINING,
                progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                progressDenominator = denominatorArr[0],
                currentProgress = getCurrentProgress("weekly", denominatorArr[0]),
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = WeeklyActivity::class.java
            ),
            DetailTrainingItem(
                id = "emotion_detail_001",
                title = "상태 기록하기",
                subtitle = "정서와 관련된 신체 감각 찾기",
                trainingType = TrainingType.EMOTION_TRAINING,
                progressNumerator = countCompleteMap["select"]?.toString() ?: "0",
                progressDenominator = denominatorArr[1],
                currentProgress = getCurrentProgress("select", denominatorArr[1]),
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = SelectActivity::class.java
            ),
            DetailTrainingItem(
                id = "emotion_detail_002",
                title = "현재에 닻 내리기",
                subtitle = "특별한 경험을 기록하기",
                trainingType = TrainingType.EMOTION_TRAINING,
                progressNumerator = countCompleteMap["anchor"]?.toString() ?: "0",
                progressDenominator = denominatorArr[2],
                currentProgress = getCurrentProgress("anchor", denominatorArr[2]),
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = AnchorActivity::class.java
            ),
            DetailTrainingItem(
                id = "emotion_detail_003",
                title = "ARC 정서 경험 기록",
                subtitle = "특별한 경험을 기록하기",
                trainingType = TrainingType.EMOTION_TRAINING,
                progressNumerator = countCompleteMap["arc"]?.toString() ?: "0",
                progressDenominator = denominatorArr[3],
                currentProgress = getCurrentProgress("arc", denominatorArr[3]),
                backgroundColorResId = R.color.button_color_emotion,
                targetActivityClass = ArcActivity::class.java
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
            val intent = Intent(this, EmotionReportActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}