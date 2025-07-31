package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.media.MediaPlayer
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
    private var totalTimeInMillis: Long = 120 * 1000

    // --- 음악 재생을 위한 변수 추가 ---
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalTimeInMillis = intent.getLongExtra("TIMER_DURATION", 120 * 1000L)

        val timerContent = layoutInflater.inflate(R.layout.content_emotion_timer, binding.pageContainer, true)
        val progressCircular = timerContent.findViewById<CircularProgressIndicator>(R.id.progress_circular)
        val tvTimer = timerContent.findViewById<TextView>(R.id.tv_timer)
        val tvGuidance = timerContent.findViewById<TextView>(R.id.tv_guidance)

        startTimer(progressCircular, tvTimer, tvGuidance)
        setupMusic()

        binding.btnBack.setOnClickListener { finish() }

        // --- '중지' 버튼의 ID를 btnStop으로 가정합니다 ---
        binding.btnStop.setOnClickListener {
            timer?.cancel()
            navigateToReflectionScreen()
        }

        // --- 소리 켜고 끄기 버튼 리스너 추가 ---
        binding.btnVolumeToggle.setOnClickListener {
            toggleMute()
        }
    }

    private fun setupMusic() {
        // res/raw 폴더에 있는 meditation_music.mp3 파일을 재생합니다.
        mediaPlayer = MediaPlayer.create(this, R.raw.meditation_music)
        mediaPlayer?.isLooping = true // 반복 재생 설정
        mediaPlayer?.start() // 음악 시작
    }

    private fun toggleMute() {
        isMuted = !isMuted
        if (isMuted) {
            mediaPlayer?.setVolume(0f, 0f) // 볼륨을 0으로 설정
            binding.btnVolumeToggle.setImageResource(R.drawable.ic_volume_off)
        } else {
            mediaPlayer?.setVolume(1f, 1f) // 볼륨을 최대로 설정
            binding.btnVolumeToggle.setImageResource(R.drawable.ic_volume_on)
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause() // 화면이 보이지 않을 때 일시정지
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start() // 화면이 다시 보일 때 재생
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        // --- 액티비티 종료 시 미디어 플레이어 자원 해제 ---
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // ... (startTimer, updateGuidanceText 등 나머지 함수는 변경 없습니다) ...

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
}