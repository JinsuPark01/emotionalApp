package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityEmotionTimerBinding
import com.google.android.material.progressindicator.CircularProgressIndicator

class EmotionTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionTimerBinding
    private var timer: CountDownTimer? = null
    private val totalTimeInMillis: Long = 120 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 타이머 콘텐츠 레이아웃을 pageContainer에 추가
        val timerContent = layoutInflater.inflate(R.layout.content_emotion_timer, binding.pageContainer, true)
        val progressCircular = timerContent.findViewById<CircularProgressIndicator>(R.id.progress_circular)
        val tvTimer = timerContent.findViewById<TextView>(R.id.tv_timer)
        val tvGuidance = timerContent.findViewById<TextView>(R.id.tv_guidance)

        startTimer(progressCircular, tvTimer, tvGuidance)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnStop.setOnClickListener {
            timer?.cancel()
            navigateToReflectionScreen()
        }
    }

    private fun startTimer(progressCircular: CircularProgressIndicator, tvTimer: TextView, tvGuidance: TextView) {
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
                updateGuidanceText(tvGuidance, secondsRemaining)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                tvGuidance.text = "수고하셨어요."
                progressCircular.progress = 0
                navigateToReflectionScreen()
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

    private fun navigateToReflectionScreen() {
        startActivity(Intent(this, EmotionReflectionActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}