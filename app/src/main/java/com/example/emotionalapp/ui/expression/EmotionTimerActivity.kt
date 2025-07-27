package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityTrainingFrameBinding
import com.google.android.material.progressindicator.CircularProgressIndicator

class EmotionTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingFrameBinding
    private var timer: CountDownTimer? = null
    private val totalTimeInMillis: Long = 120 * 1000

    // 내용물 뷰들을 저장할 변수
    private lateinit var progressCircular: CircularProgressIndicator
    private lateinit var tvTimer: TextView
    private lateinit var tvGuidance: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        startTimer()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.navPage.btnNext.setOnClickListener {
            // '중지' 버튼의 역할
            timer?.cancel()
            navigateToReflectionScreen()
        }
    }

    private fun setupUI() {
        // 상단 바 설정
        binding.titleText.text = "정서 머무르기"
        binding.tabRecord.visibility = View.GONE
        binding.underlineRecord.visibility = View.GONE
        binding.tabPracticeContainer.layoutParams = (binding.tabPracticeContainer.layoutParams as LinearLayout.LayoutParams).apply {
            width = 0
            weight = 2f
        }

        // 하단 네비게이션 바 설정
        binding.navPage.btnPrev.visibility = View.INVISIBLE
        binding.navPage.indicatorContainer.visibility = View.GONE
        binding.navPage.btnNext.text = "중지"
    }

    private fun startTimer() {
        // --- 여기가 핵심 수정 부분입니다 ---
        val timerContent = layoutInflater.inflate(R.layout.content_emotion_timer, binding.pageContainer, true)

        // findViewById를 사용하여 뷰들을 초기화
        progressCircular = timerContent.findViewById(R.id.progress_circular)
        tvTimer = timerContent.findViewById(R.id.tv_timer)
        tvGuidance = timerContent.findViewById(R.id.tv_guidance)

        val totalSeconds = (totalTimeInMillis / 1000).toInt()
        progressCircular.max = totalSeconds
        progressCircular.progress = totalSeconds

        timer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                progressCircular.progress = secondsRemaining.toInt()
                updateGuidanceText(secondsRemaining)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                tvGuidance.text = "수고하셨어요. 감정을 없애려 하지 않고\n잠시 바라본 것만으로도 충분합니다."
                progressCircular.progress = 0
                navigateToReflectionScreen()
            }
        }.start()
    }

    private fun updateGuidanceText(secondsRemaining: Long) {
        val guidanceText = when {
            secondsRemaining <= 90 && secondsRemaining > 60 -> "지금 느껴지는 감정에 집중해볼까요?\n그 감정은 어디에서 시작되었나요?"
            secondsRemaining <= 60 && secondsRemaining > 30 -> "이 감정을 느껴도 괜찮아요."
            secondsRemaining <= 30 && secondsRemaining > 0 -> "숨을 천천히 쉬면서,\n감정을 그냥 거기에 두세요."
            else -> "그 감정을 억누르지 말고,\n지금 이 순간 그대로 느껴보세요."
        }
        tvGuidance.text = guidanceText
    }

    private fun navigateToReflectionScreen() {
        val intent = Intent(this, EmotionReflectionActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}