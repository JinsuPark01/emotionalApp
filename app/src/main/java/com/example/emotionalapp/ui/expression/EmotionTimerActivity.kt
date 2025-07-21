package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityEmotionTimerBinding
import com.example.emotionalapp.ui.alltraining.ExpressionActivity

class EmotionTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionTimerBinding
    private var timer: CountDownTimer? = null

    // 2분 = 120초
    private val totalTimeInMillis: Long = 120 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStop.setOnClickListener {
            timer?.cancel()
            returnToTrainingList()
        }

        startTimer()
    }

    private fun startTimer() {
        // --- 여기가 핵심 수정 부분입니다 ---
        val totalSeconds = (totalTimeInMillis / 1000).toInt()
        binding.progressCircular.max = totalSeconds
        binding.progressCircular.progress = totalSeconds // 시작 시 100%로 설정

        timer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000

                // 타이머 텍스트 업데이트 (예: 02:00, 01:59 ...)
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)

                // 프로그레스 바 업데이트
                binding.progressCircular.setProgressCompat(secondsRemaining.toInt(), true)

                // 텍스트 안내 메시지 업데이트
                updateGuidanceText(secondsRemaining)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.tvGuidance.text = "수고하셨어요. 감정을 없애려 하지 않고\n잠시 바라본 것만으로도 충분합니다."
                binding.btnStop.text = "완료"
                binding.progressCircular.progress = 0
            }
        }.start()
    }

    private fun updateGuidanceText(secondsRemaining: Long) {
        val guidanceText = when {
            // 시작 후 (예: 1분 30초 남았을 때)
            secondsRemaining <= 90 && secondsRemaining > 60 -> "지금 느껴지는 감정에 집중해볼까요?\n그 감정은 어디에서 시작되었나요?"
            // 중간 (예: 1분 남았을 때)
            secondsRemaining <= 60 && secondsRemaining > 30 -> "이 감정을 느껴도 괜찮아요."
            // 거의 끝날 때 (예: 30초 남았을 때)
            secondsRemaining <= 30 && secondsRemaining > 0 -> "숨을 천천히 쉬면서,\n감정을 그냥 거기에 두세요."
            // 시작 시
            else -> "그 감정을 억누르지 말고,\n지금 이 순간 그대로 느껴보세요."
        }
        binding.tvGuidance.text = guidanceText
    }

    private fun returnToTrainingList() {
        val intent = Intent(this, ExpressionActivity::class.java).apply {
            // 이전에 열렸던 모든 액티비티(감정선택, 목차 등)를 스택에서 제거하고,
            // 4주차 훈련 목록 화면을 새로운 시작점으로 엽니다.
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish() // 현재 타이머 액티비티 종료
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // 액티비티가 종료될 때 타이머도 확실히 종료
    }
}