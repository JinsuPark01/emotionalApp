package com.example.emotionalapp.ui.expression // 올바른 패키지 경로로 수정

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityEmotionSelectionBinding

class EmotionSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionSelectionBinding
    private var selectedEmotion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEmotionGrid()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartTimer.setOnClickListener {
            selectedEmotion?.let {
                val intent = Intent(this, EmotionTimerActivity::class.java).apply {
                    putExtra("SELECTED_EMOTION", it)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupEmotionGrid() {
        val emotions = listOf(
            "짜증", "분노", "외로움", "불안", "슬픔", "죄책감", "피곤함", "무기력", "서운함"
        )

        for (emotion in emotions) {
            val emotionView = createEmotionTextView(emotion)
            binding.gridEmotions.addView(emotionView)
        }
    }

    private fun createEmotionTextView(emotion: String): TextView {
        return TextView(this).apply {
            text = emotion
            layoutParams = android.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = android.widget.GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            background = ContextCompat.getDrawable(this@EmotionSelectionActivity, R.drawable.bg_topic_button)
            gravity = Gravity.CENTER
            setPadding(16, 24, 16, 24)
            setOnClickListener {
                // 모든 버튼의 선택 상태 초기화
                for (i in 0 until binding.gridEmotions.childCount) {
                    val child = binding.gridEmotions.getChildAt(i)
                    child.background = ContextCompat.getDrawable(this@EmotionSelectionActivity, R.drawable.bg_topic_button)
                }

                // 현재 선택된 버튼 강조
                it.background = ContextCompat.getDrawable(this@EmotionSelectionActivity, R.drawable.bg_round_green_button)?.apply {
                    setTint(ContextCompat.getColor(this@EmotionSelectionActivity, R.color.purple_500))
                }

                selectedEmotion = emotion
                binding.btnStartTimer.isEnabled = true
            }
        }
    }
}