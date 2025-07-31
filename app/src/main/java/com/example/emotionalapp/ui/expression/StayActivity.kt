package com.example.emotionalapp.ui.expression

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

class StayActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val totalPages = 4
    private var currentPage = 0

    // 0페이지 감정 선택 및 타이머 분 선택 관련
    private var selectedEmotion: String? = null
    private var selectedEmotionView: View? = null
    private var selectedTimerMillis: Long = 120 * 1000L // 기본 2분

    // 2페이지 감정 기록 입력값
    private var clarifiedEmotion: String = ""
    private var moodChanged: String = ""

    // 1페이지 타이머 + 음악
    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false

    private var saveJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        setupIndicators()
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                if (currentPage == 1) {
                    stopTimerAndMusic()
                }
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            when (currentPage) {
                0 -> {
                    if (selectedEmotion == null) {
                        Toast.makeText(this, "오늘의 감정을 선택해주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    // 타이머 분 선택은 이미 selectedTimerMillis에 저장됨
                    currentPage++
                    updatePage()
                }
                1 -> {
                    stopTimerAndMusic()
                    currentPage++
                    updatePage()
                }
                2 -> {
                    val pageView = pageContainer.getChildAt(0)
                    val input1 = pageView.findViewById<EditText>(R.id.edit_text_emotion_clarified)
                    val input2 = pageView.findViewById<EditText>(R.id.edit_text_mood_changed)

                    clarifiedEmotion = input1.text.toString().trim()
                    moodChanged = input2.text.toString().trim()

                    if (clarifiedEmotion.isEmpty() || moodChanged.isEmpty()) {
                        Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    currentPage++
                    updatePage()
                }
                3 -> {
                    if (saveJob?.isActive == true) return@setOnClickListener
                    btnNext.isEnabled = false

                    saveJob = lifecycleScope.launch {
                        val success = withContext(Dispatchers.IO) { saveToFirestore() }
                        btnNext.isEnabled = true

                        if (success) {
                            Toast.makeText(this@StayActivity, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@StayActivity, AllTrainingPageActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@StayActivity, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun stopTimerAndMusic() {
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private suspend fun saveToFirestore(): Boolean = suspendCancellableCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser ?: return@suspendCancellableCoroutine continuation.resume(false)
        val email = user.email ?: return@suspendCancellableCoroutine continuation.resume(false)

        val timestamp = Timestamp.now()
        val dateString = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.format(timestamp.toDate())

        // 저장 데이터에 선택한 감정과 타이머 분수 추가
        val data = hashMapOf(
            "type" to "emotionStay",
            "date" to timestamp,
            "emotion" to selectedEmotion,
            "answer1" to clarifiedEmotion,
            "answer2" to moodChanged
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("user")
            .document(email)
            .collection("expressionStay")
            .document(dateString)
            .set(data)
            .addOnSuccessListener {
                db.collection("user")
                    .document(email)
                    .update("countComplete.stay", FieldValue.increment(1))
                    .addOnSuccessListener { continuation.resume(true) }
                    .addOnFailureListener { continuation.resume(false) }
            }
            .addOnFailureListener { continuation.resume(false) }
    }

    private fun setupIndicators() {
        indicatorContainer.removeAllViews()
        repeat(totalPages) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    setMargins(8, 0, 8, 0)
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (currentPage) {
            0, 1 -> "정서 머무르기"
            2 -> "감정 기록하기"
            3 -> "마무리"
            else -> ""
        }

        val layoutRes = when (currentPage) {
            0 -> R.layout.fragment_expression_stay_training_0
            1 -> R.layout.fragment_expression_stay_training_1
            2 -> R.layout.fragment_expression_stay_training_2
            3 -> R.layout.fragment_expression_stay_training_3
            else -> R.layout.fragment_expression_stay_training_0
        }

        val view = inflater.inflate(layoutRes, pageContainer, false)
        pageContainer.addView(view)

        when (currentPage) {
            0 -> setupPage0(view)
            1 -> setupPage1(view)
            2 -> setupPage2(view)
            3 -> { /* 특별 처리 없음 */ }
        }

        btnPrev.isEnabled = currentPage != 0 && currentPage != 2
        btnPrev.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (btnPrev.isEnabled) "#3CB371" else "#D9D9D9")
        )

        btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            indicatorContainer.getChildAt(i).setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }
    }

    // 0페이지 : 감정 선택 + 타이머 분 선택
    private fun setupPage0(view: View) {
        val gridEmotions = view.findViewById<GridLayout>(R.id.grid_emotions)
        gridEmotions.removeAllViews()

        val emotions = mapOf(
            "행복" to R.drawable.emotion_happy,
            "즐거움" to R.drawable.emotion_joy,
            "자신감" to R.drawable.emotion_confident,
            "슬픔" to R.drawable.emotion_sad,
            "두려움" to R.drawable.emotion_fear,
            "당황" to R.drawable.emotion_embarrassed,
            "걱정" to R.drawable.emotion_anxious,
            "짜증" to R.drawable.emotion_annoyed,
            "분노" to R.drawable.emotion_angry
        )

        for ((emotionText, iconResId) in emotions) {
            val emotionView = createEmotionView(emotionText, iconResId, gridEmotions)
            gridEmotions.addView(emotionView)
        }

        // 이전에 선택한 감정 표시
        selectedEmotionView?.let {
            it.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
                setTint(ContextCompat.getColor(this@StayActivity, R.color.purple_500))
            }
            (it as LinearLayout).findViewById<TextView>(R.id.tv_emotion).setTextColor(ContextCompat.getColor(this, android.R.color.white))
        }

        // 타이머 라디오 버튼 초기화 및 리스너
        val rgTimer = view.findViewById<RadioGroup>(R.id.rg_timer_duration)
        when (selectedTimerMillis) {
            60_000L -> rgTimer.check(R.id.rb_1_min)
            180_000L -> rgTimer.check(R.id.rb_3_min)
            else -> rgTimer.check(R.id.rb_2_min)
        }
        rgTimer.setOnCheckedChangeListener { _, checkedId ->
            selectedTimerMillis = when (checkedId) {
                R.id.rb_1_min -> 60 * 1000L
                R.id.rb_3_min -> 180 * 1000L
                else -> 120 * 1000L
            }
        }
    }

    private fun createEmotionView(emotion: String, iconResId: Int, parent: GridLayout): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_emotion_card, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.iv_emotion)
        val textView = view.findViewById<TextView>(R.id.tv_emotion)

        imageView.setImageResource(iconResId)
        textView.text = emotion

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(8, 8, 8, 8)
        }
        view.layoutParams = params

        view.setOnClickListener {
            // 이전 선택 초기화
            selectedEmotionView?.let { oldView ->
                oldView.background = ContextCompat.getDrawable(this, R.drawable.bg_topic_button)
                oldView.findViewById<TextView>(R.id.tv_emotion).setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }

            // 새 선택 표시
            it.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
                setTint(ContextCompat.getColor(this@StayActivity, R.color.purple_500))
            }
            (it as LinearLayout).findViewById<TextView>(R.id.tv_emotion).setTextColor(ContextCompat.getColor(this, android.R.color.white))

            selectedEmotionView = it
            selectedEmotion = emotion
        }

        return view
    }

    // 1페이지 : 타이머 + 음악 재생 + 음소거
    private fun setupPage1(view: View) {
        val progressCircular = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)
        val tvTimer = view.findViewById<TextView>(R.id.tv_timer)
        val tvGuidance = view.findViewById<TextView>(R.id.tv_guidance)
        val btnVolumeToggle = view.findViewById<ImageView>(R.id.btn_volume_toggle)

        startTimer(progressCircular, tvTimer, tvGuidance)
        setupMusic()
        updateVolumeIcon(btnVolumeToggle)

        btnVolumeToggle.setOnClickListener {
            toggleMute()
            updateVolumeIcon(btnVolumeToggle)
        }
    }

    private fun startTimer(progressCircular: CircularProgressIndicator, tvTimer: TextView, tvGuidance: TextView) {
        val totalSeconds = (selectedTimerMillis / 1000).toInt()
        progressCircular.max = totalSeconds
        progressCircular.progress = totalSeconds

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(selectedTimerMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                progressCircular.progress = secondsRemaining.toInt()
                updateGuidanceText(tvGuidance, secondsRemaining)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                tvGuidance.text = "수고하셨어요."
                progressCircular.progress = 0
            }
        }.start()
    }

    private fun updateGuidanceText(tvGuidance: TextView, secondsRemaining: Long) {
        val guidanceText = when {
            secondsRemaining <= 90 && secondsRemaining > 60 -> "지금 느껴지는 감정에 집중해볼까요?"
            secondsRemaining <= 60 && secondsRemaining > 30 -> "이 감정을 느껴도 괜찮아요."
            secondsRemaining <= 30 && secondsRemaining > 0 -> "숨을 천천히 쉬면서, 감정을 그냥 거기에 두세요."
            else -> "그 감정을 억누르지 말고, 지금 이 순간 그대로 느껴보세요."
        }
        tvGuidance.text = guidanceText
    }

    private fun setupMusic() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.meditation_music)
        mediaPlayer?.isLooping = true
        if (!isMuted) {
            mediaPlayer?.setVolume(1f, 1f)
            mediaPlayer?.start()
        }
    }

    private fun toggleMute() {
        isMuted = !isMuted
        if (isMuted) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(1f, 1f)
            mediaPlayer?.start()
        }
    }

    private fun updateVolumeIcon(btnVolumeToggle: ImageView) {
        if (isMuted) {
            btnVolumeToggle.setImageResource(R.drawable.ic_volume_off)
        } else {
            btnVolumeToggle.setImageResource(R.drawable.ic_volume_on)
        }
    }

    // 2페이지 : 감정 기록 입력
    private fun setupPage2(view: View) {
        val etClarifyEmotion = view.findViewById<EditText>(R.id.edit_text_emotion_clarified)
        val etMoodChange = view.findViewById<EditText>(R.id.edit_text_mood_changed)

        etClarifyEmotion.setText(clarifiedEmotion)
        etMoodChange.setText(moodChanged)
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!isMuted) {
            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
