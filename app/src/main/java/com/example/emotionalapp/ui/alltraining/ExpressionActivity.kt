package com.example.emotionalapp.ui.alltraining

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.DetailTrainingAdapter
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.databinding.ActivityDetailTrainingBinding
import com.example.emotionalapp.ui.expression.*
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ExpressionActivity : BottomNavActivity() {

    private lateinit var binding: ActivityDetailTrainingBinding
    private lateinit var adapter: DetailTrainingAdapter
    override val isAllTrainingPage: Boolean = true
    private var userDiffDays: Long = 0
    private var countCompleteMap: Map<String, Long> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
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
                                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).apply { timeZone = koreaTimeZone }.format(Date())

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

                    onFinished()
                }
                .addOnFailureListener { e ->
                    Log.e("UserJoinDate", "Firestore 에러: ${e.message}")
                    onFinished()
                }
        } else {
            onFinished()
        }
    }

    private fun setupRecyclerView() {
        adapter = DetailTrainingAdapter(emptyList()) { item ->
            if (item.currentProgress == "잠김") {
                Toast.makeText(this, "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            } else if (item.progressDenominator == item.progressNumerator && item.currentProgress != "GO") {
                Toast.makeText(this, "모두 완료한 훈련입니다.", Toast.LENGTH_SHORT).show()
                return@DetailTrainingAdapter
            }

            if (item.id == "driven_action_training") {
                AlertDialog.Builder(this)
                    .setTitle("대안 행동 찾기 가이드")
                    .setMessage(
                        "감정은 누구나 느껴요.\n" +
                                "중요한 건, 그 감정을 느낄 때 어떻게 행동하느냐예요.\n\n" +
                                "지금부터 당신은 감정에 끌려가기보단,\n" +
                                "직접 선택한 대안 행동을 해보고,\n" +
                                "그 결과가 어땠는지 돌아볼 시간을 가질 거예요.\n\n" +
                                "감정을 '없애는' 게 아니라, 감정을 다루는 자신만의 방법을 만들어보는 시간이에요.\n" +
                                "편하게 선택하고 적어보세요!"
                    )
                    .setPositiveButton("시작하기") { _, _ ->
                        item.targetActivityClass?.let { targetClass ->
                            val intent = Intent(this, targetClass)
                            startActivity(intent)
                        }
                    }
                    .setNegativeButton("취소", null)
                    .show()
            } else {
                item.targetActivityClass?.let { targetClass ->
                    val intent = Intent(this, targetClass)
                    startActivity(intent)
                }
            }
        }
        binding.trainingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainingRecyclerView.adapter = adapter
    }

    private fun loadTrainingData() {
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

    // --- 여기가 핵심 수정 부분입니다 ---
    private fun setupTabListeners() {
        binding.tvPageTitle.text = "4주차 훈련"

        // '전체 훈련' 탭 (현재 페이지)
        binding.tabAll.setOnClickListener {
            Log.d("ExpressionActivity", "전체 훈련 탭 클릭됨 (현재 페이지)")
            binding.underlineAll.visibility = View.VISIBLE
            binding.underlineRecord.visibility = View.INVISIBLE
            // 현재 화면이므로 아무것도 하지 않음
        }

        // '기록 보기' 탭
        binding.tabRecord.setOnClickListener {
            Log.d("ExpressionActivity", "기록 보기 탭 클릭됨 - ExpressionReportActivity로 이동")
            binding.underlineAll.visibility = View.INVISIBLE
            binding.underlineRecord.visibility = View.VISIBLE

            val intent = Intent(this, ExpressionReportActivity::class.java)
            // finish()를 호출하면, 기록 보기에서 뒤로 왔을 때 앱이 종료되므로 삭제합니다.
            startActivity(intent)
        }
    }
}