package com.example.emotionalapp.ui.body

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

class BodyTrainingPracticeActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStopPractice: Button
    private lateinit var btnRecord: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var tvPracticeDetail: TextView

    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val pos = mediaPlayer.currentPosition
                progressBar.progress = pos
                tvCurrentTime.text = formatTime(pos)
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice)

        // 뒤로가기
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // Intent로부터 데이터 수신
        val trainingId = intent.getIntExtra("TRAINING_ID", 2)
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "연습"

        // 훈련 ID 문자열로 매핑
        val trainingIdStr = when (trainingId) {
            2 -> "bt_detail_002"
            3 -> "bt_detail_003"
            4 -> "bt_detail_004"
            5 -> "bt_detail_005"
            6 -> "bt_detail_006"
            7 -> "bt_detail_007"
            8 -> "bt_detail_008"
            else -> "bt_detail_002"
        }

        // 뷰 바인딩
        btnStart = findViewById(R.id.btnStart)
        btnStopPractice = findViewById(R.id.btnStopPractice)
        btnRecord = findViewById(R.id.btnRecord)
        progressBar = findViewById(R.id.progressBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        val tvTitle = findViewById<TextView>(R.id.tv_practice_title)
        tvPracticeDetail = findViewById(R.id.tv_practice_detail)

        // 제목 및 상세 내용 설정
        tvTitle.text = trainingTitle
        tvPracticeDetail.text = when (trainingId) {
            2 -> "DAY 1 연습"
            3 -> "DAY 2 연습"
            4 -> "DAY 3 연습"
            5 -> "DAY 4 연습"
            6 -> "DAY 5 연습"
            7 -> "DAY 6 연습"
            8 -> "DAY 7 연습"
            else -> "준비 중인 연습입니다."
        }

        // MediaPlayer 초기화 (현재는 test_audio.mp3 한 개만 사용)
        mediaPlayer = MediaPlayer.create(this, getAudioResId(trainingId)).apply {
            isLooping = false
        }
        val duration = mediaPlayer.duration
        progressBar.max = duration
        tvTotalTime.text = formatTime(duration)

        // 버튼 리스너
        btnStart.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                handler.post(updateRunnable)
            }
        }

        btnStopPractice.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                handler.removeCallbacks(updateRunnable)
            }
        }

        btnRecord.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                handler.removeCallbacks(updateRunnable)
            }
            startActivity(Intent(this, BodyTrainingRecordActivity::class.java).apply {
                putExtra("TRAINING_ID", trainingIdStr) // 🔧 수정된 부분
            })
        }

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateRunnable)
            progressBar.progress = progressBar.max
            tvCurrentTime.text = formatTime(duration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%d:%02d", min, sec)
    }

    private fun getAudioResId(id: Int): Int = R.raw.test_audio
}


// 훈련 ID → raw 리소스 매핑 함수
//    private fun getAudioResId(id: Int): Int = when (id) {
//        2 -> R.raw.training_002
//        3 -> R.raw.training_003
//        4 -> R.raw.training_004
//        5 -> R.raw.training_005
//        6 -> R.raw.training_006
//        7 -> R.raw.training_007
//        8 -> R.raw.training_008
//        else -> R.raw.training_002
//    }

