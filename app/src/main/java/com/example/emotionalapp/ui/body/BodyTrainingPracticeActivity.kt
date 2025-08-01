package com.example.emotionalapp.ui.body

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
    private lateinit var videoView: VideoView

    private val handler = Handler(Looper.getMainLooper())

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (videoView.isPlaying) {
                val position = videoView.currentPosition
                progressBar.progress = position
                tvCurrentTime.text = formatTime(position)
                handler.postDelayed(this, 500)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice)

        val trainingIdStr = intent.getStringExtra("TRAINING_ID") ?: "bt_detail_002"
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "연습"

        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ ->
                    finish()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

        tvPracticeDetail = findViewById(R.id.tv_practice_detail)
        val tvTitle = findViewById<TextView>(R.id.tv_practice_title)
        tvTitle.text = trainingTitle

        btnStart = findViewById(R.id.btnStart)
        btnStopPractice = findViewById(R.id.btnStopPractice)
        btnRecord = findViewById(R.id.btnRecord)
        progressBar = findViewById(R.id.progressBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        videoView = findViewById(R.id.videoView)

        // 비디오 URI 설정
        val videoResId = getVideoResId(trainingIdStr)
        if (videoResId != null) {
            val uri = Uri.parse("android.resource://${packageName}/$videoResId")
            videoView.setVideoURI(uri)
            videoView.visibility = View.VISIBLE
            videoView.setOnPreparedListener { mp ->
                val duration = mp.duration
                progressBar.max = duration
                tvTotalTime.text = formatTime(duration)
            }
            videoView.setOnCompletionListener {
                handler.removeCallbacks(updateProgressRunnable)
                progressBar.progress = progressBar.max
                tvCurrentTime.text = formatTime(progressBar.max)

                // ⏩ 자동으로 소감 작성 페이지로 이동
                val intent = Intent(this, BodyTrainingRecordActivity::class.java)
                intent.putExtra("TRAINING_ID", trainingIdStr)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "비디오 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }

        btnStart.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
                handler.post(updateProgressRunnable)
            }
        }

        btnStopPractice.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                handler.removeCallbacks(updateProgressRunnable)
            }
        }

        btnRecord.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                handler.removeCallbacks(updateProgressRunnable)
            }
            val intent = Intent(this, BodyTrainingRecordActivity::class.java)
            intent.putExtra("TRAINING_ID", trainingIdStr)
            startActivity(intent)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        if (::videoView.isInitialized) {
            videoView.stopPlayback()
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun getVideoResId(idStr: String): Int? {
        return when (idStr) {
            "bt_detail_002" -> R.raw.video_bt_detail_002
            "bt_detail_003" -> R.raw.video_bt_detail_003
            "bt_detail_004" -> R.raw.video_bt_detail_004
            "bt_detail_005" -> R.raw.video_bt_detail_005
            "bt_detail_006" -> R.raw.video_bt_detail_006
            "bt_detail_007" -> R.raw.video_bt_detail_007
            "bt_detail_008" -> R.raw.video_bt_detail_008
            else -> null
        }
    }
}
