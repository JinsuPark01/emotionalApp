package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityEmotionTimerBinding

class EmotionTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionTimerBinding
    private var timer: CountDownTimer? = null

    private val totalTimeInMillis: Long = 120 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStop.setOnClickListener {
            timer?.cancel()
            // --- 중지 버튼 클릭 시 감정 기록 화면으로 이동 ---
            navigateToReflectionScreen()
        }

        startTimer()
    }

    private fun startTimer() {
        val totalSeconds = (totalTimeInMillis / 1000).toInt()
        binding.progressCircular.max = totalSeconds
        binding.progressCircular.progress = totalSeconds

        timer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                binding.progressCircular.setProgressCompat(secondsRemaining.toInt(), true)
                updateGuidanceText(secondsRemaining)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.tvGuidance.text = "수고하셨어요. 감정을 없애려 하지 않고\n잠시 바라본 것만으로도 충분합니다."
                binding.btnStop.text = "기록하기" // 버튼 텍스트 변경
                binding.progressCircular.progress = 0
                // --- 타이머 완료 시 감정 기록 화면으로 이동 ---
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
        binding.tvGuidance.text = guidanceText
    }

    // --- 새로운 함수 추가 ---
    private fun navigateToReflectionScreen() {
        // 타이머가 끝나면 감정 기록 화면으로 이동
        val intent = Intent(this, EmotionReflectionActivity::class.java)
        startActivity(intent)
        finish() // 현재 타이머 액티비티는 종료
    }

    // --- 기존 returnToTrainingList() 함수는 이제 사용되지 않으므로 삭제해도 됩니다 ---

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}