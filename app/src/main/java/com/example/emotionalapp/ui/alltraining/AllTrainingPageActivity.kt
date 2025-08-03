package com.example.emotionalapp.ui.alltraining

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AllTrainingAdapter
import com.example.emotionalapp.data.TrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.databinding.ActivityAllTrainingBinding
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.example.emotionalapp.ui.open.BottomNavActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AllTrainingPageActivity : BottomNavActivity() {

    // 뷰 바인딩으로 전환
    private lateinit var binding: ActivityAllTrainingBinding
    private lateinit var trainingAdapter: AllTrainingAdapter

    override val isAllTrainingPage: Boolean = true

    private var userDiffDays: Long = 0
    private var countCompleteMap: Map<String, Long> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 바인딩 설정
        binding = ActivityAllTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupRecyclerView()
        calculateDiffDaysAndGetCount{loadTrainingData()}
        setupTabListeners()
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
        // 어댑터 초기화 시, 아이템에 지정된 targetActivityClass로 바로 이동
        trainingAdapter = AllTrainingAdapter(emptyList()) { clickedTrainingItem ->

            // "잠김"일 경우 클릭 무시 또는 토스트 메시지 출력
            if (clickedTrainingItem.currentProgress == "잠김") {
                Toast.makeText(this, "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                return@AllTrainingAdapter
            }

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
        checkAndInsertMissedWeeklies()
        if(userDiffDays == 28L){
            AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("오늘은 훈련 마지막 날입니다. 오늘이면 훈련은 끝나지만, 앞으로도 훈련을 통해 배운 것을 활용하여 일상 속에서 감정을 잘 알아차리고 조절해보아요.")
                .setPositiveButton("확인", null)
                .setCancelable(false)
                .show()
        }else if (userDiffDays > 28L) {
            AlertDialog.Builder(this)
                .setTitle("안내")
                .setMessage("전체 훈련이 마무리되었습니다. 훈련은 끝났지만, 앞으로도 훈련을 통해 배운 것을 활용하여 일상 속에서 감정을 잘 알아차리고 조절해보아요.")
                .setPositiveButton("확인", null)
                .setCancelable(false)
                .show()
        }
        var progressArr: Array<String> = when (userDiffDays) {
            in 1..7 -> {arrayOf("GO", "GO", "잠김", "잠김", "잠김") }
            in 8..14 -> {arrayOf("GO", "GO", "GO", "잠김", "잠김") }
            in 15..21 -> {arrayOf("GO", "GO", "GO", "GO", "잠김") }
            in 22..28 -> {arrayOf("GO", "GO", "GO", "GO", "GO") }
            else -> {arrayOf("GO", "GO", "GO", "GO", "GO") }
        }
        // 각 아이템에 이동할 Activity 클래스를 직접 지정
        val sampleData = listOf(
            TrainingItem(
                "intro001",
                "INTRO",
                "감정의 세계로 떠나는 첫 걸음",
                TrainingType.INTRO,
                progressArr[0],
                R.color.button_color_intro,
                IntroActivity::class.java
            ),
            TrainingItem(
                "et001",
                "1주차 - 정서인식 훈련",
                "나의 감정을 정확히 알아차리기",
                TrainingType.EMOTION_TRAINING,
                progressArr[1],
                R.color.button_color_emotion,
                EmotionActivity::class.java
            ),
            TrainingItem(
                "bt001",
                "2주차 - 신체자각 훈련",
                "몸이 보내는 신호에 귀 기울이기",
                TrainingType.BODY_TRAINING,
                progressArr[2],
                R.color.button_color_body,
                BodyActivity::class.java
            ),
            TrainingItem(
                "mwt001",
                "3주차 - 인지재구성 훈련",
                "생각의 틀을 바꾸는 연습",
                TrainingType.MIND_WATCHING_TRAINING,
                progressArr[3],
                R.color.button_color_mind,
                MindActivity::class.java
            ),
            TrainingItem(
                "eat001",
                "4주차 - 정서표현 및 행동 훈련",
                "건강하게 감정을 표현하고 행동하기",
                TrainingType.EXPRESSION_ACTION_TRAINING,
                progressArr[4],
                R.color.button_color_expression,
                ExpressionActivity::class.java
            )
        )
        trainingAdapter.updateData(sampleData)
    }

    private fun insertDummyWeekly() {
        // Firestore에 저장
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        val nowTimestamp = Timestamp.now()
        val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.format(nowTimestamp.toDate())
        val dummyData = hashMapOf(
            "type" to "weekly3",
            "date" to nowTimestamp,
            "phq9" to hashMapOf(
                "answers" to List(9) { -1 },  // 전혀 선택 안 했다는 의미
                "sum" to -1
            ),
            "gad7" to hashMapOf(
                "answers" to List(7) { -1 },
                "sum" to -1
            ),
            "panas" to hashMapOf(
                "answers" to List(20) { -1 },  // PANAS는 보통 20문항
                "positiveSum" to -1,
                "negativeSum" to -1
            )
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("user")
            .document(userEmail)
            .collection("weekly3")
            .document(today) // 문서명 중복 방지용
            .set(dummyData)
            .addOnSuccessListener {
                Log.d("Firestore", "더미 weekly3 저장 성공")
                // 저장 성공 시에만 countComplete.weekly +1
                db.collection("user")
                    .document(userEmail)
                    .update("countComplete.weekly", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Log.d("Firestore", "카운트 증가 성공")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "카운트 증가 실패", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "더미 weekly3 저장 실패", e)
            }
    }

    private fun checkAndInsertMissedWeeklies() {
        val currentWeek = ((userDiffDays - 1) / 7).toInt()
        val completedWeeklies = countCompleteMap["weekly"]?.toInt() ?: 0

        val missingCount = currentWeek - completedWeeklies

        repeat(missingCount) {
            insertDummyWeekly()
        }

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
