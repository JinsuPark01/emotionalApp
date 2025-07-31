package com.example.emotionalapp.ui.body

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
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

        // 뒤로가기 버튼
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // ✅ 수정된 부분: trainingId를 String으로 받도록 변경
        val trainingIdStr = intent.getStringExtra("TRAINING_ID") ?: "bt_detail_002"
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "연습"

        // 훈련 ID로 DAY 텍스트 매핑
        val tvTitle = findViewById<TextView>(R.id.tv_practice_title)
        tvPracticeDetail = findViewById(R.id.tv_practice_detail)

        tvTitle.text = trainingTitle
        tvPracticeDetail.text = when (trainingIdStr) {
            "bt_detail_002" -> "DAY 1 연습"
            "bt_detail_003" -> "DAY 2 연습"
            "bt_detail_004" -> "DAY 3 연습"
            "bt_detail_005" -> "DAY 4 연습"
            "bt_detail_006" -> "DAY 5 연습"
            "bt_detail_007" -> "DAY 6 연습"
            "bt_detail_008" -> "DAY 7 연습"
            else -> "준비 중인 연습입니다."
        }

        // 오디오 초기화
        mediaPlayer = MediaPlayer.create(this, getAudioResId(trainingIdStr)).apply {
            isLooping = false
        }
        val duration = mediaPlayer.duration

        btnStart = findViewById(R.id.btnStart)
        btnStopPractice = findViewById(R.id.btnStopPractice)
        btnRecord = findViewById(R.id.btnRecord)
        progressBar = findViewById(R.id.progressBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)

        progressBar.max = duration
        tvTotalTime.text = formatTime(duration)

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

            val intent = Intent(this, BodyTrainingRecordActivity::class.java)
            intent.putExtra("TRAINING_ID", trainingIdStr)
            startActivity(intent)
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

    private fun getAudioResId(idStr: String): Int {
        return when (idStr) {
            "bt_detail_002" -> R.raw.test_audio
            "bt_detail_003" -> R.raw.test_audio
            "bt_detail_004" -> R.raw.test_audio
            "bt_detail_005" -> R.raw.test_audio
            "bt_detail_006" -> R.raw.test_audio
            "bt_detail_007" -> R.raw.test_audio
            "bt_detail_008" -> R.raw.test_audio
            else -> R.raw.test_audio
        }
    }

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

