package com.example.emotionalapp.ui.expression

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
                // --- 여기가 핵심 수정 부분입니다 (1) ---
                // 선택된 라디오 버튼에 따라 타이머 시간을 결정
                val selectedDurationInMillis = when (binding.rgTimerDuration.checkedRadioButtonId) {
                    R.id.rb_1_min -> 60 * 1000L
                    R.id.rb_3_min -> 180 * 1000L
                    else -> 120 * 1000L // 기본값 2분
                }

                val intent = Intent(this, EmotionTimerActivity::class.java).apply {
                    putExtra("SELECTED_EMOTION", it)
                    // 선택한 시간을 Intent에 담아 전달
                    putExtra("TIMER_DURATION", selectedDurationInMillis)
                }
                startActivity(intent)
            }
        }
    }

    // ... (setupEmotionGrid, createEmotionTextView 함수는 변경 없습니다) ...
    private fun setupEmotionGrid() {
        val emotions = mapOf(
            "짜증" to R.drawable.ic_face_annoyed,
            "분노" to R.drawable.ic_face_anger,
            "외로움" to R.drawable.ic_face_lonely,
            "불안" to R.drawable.ic_face_anxiety,
            "슬픔" to R.drawable.ic_face_sad,
            "죄책감" to R.drawable.ic_face_guilt,
            "피곤함" to R.drawable.ic_face_fatigue,
            "무기력" to R.drawable.ic_face_lethargy,
            "서운함" to R.drawable.ic_face_upset
        )

        for ((emotionText, iconResId) in emotions) {
            val emotionView = createEmotionTextView(emotionText, iconResId)
            binding.gridEmotions.addView(emotionView)
        }
    }

    private fun createEmotionTextView(emotion: String, iconResId: Int): TextView {
        return TextView(this).apply {
            text = emotion
            setCompoundDrawablesWithIntrinsicBounds(0, iconResId, 0, 0)
            compoundDrawablePadding = 12

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
                for (i in 0 until binding.gridEmotions.childCount) {
                    val child = binding.gridEmotions.getChildAt(i) as TextView
                    child.background = ContextCompat.getDrawable(this@EmotionSelectionActivity, R.drawable.bg_topic_button)
                    child.compoundDrawables[1]?.setTintList(null)
                    child.setTextColor(ContextCompat.getColor(this@EmotionSelectionActivity, android.R.color.black))
                }

                it.background = ContextCompat.getDrawable(this@EmotionSelectionActivity, R.drawable.bg_round_green_button)?.apply {
                    setTint(ContextCompat.getColor(this@EmotionSelectionActivity, R.color.purple_500))
                }
                (it as TextView).compoundDrawables[1]?.setTint(ContextCompat.getColor(this@EmotionSelectionActivity, android.R.color.white))
                it.setTextColor(ContextCompat.getColor(this@EmotionSelectionActivity, android.R.color.white))

                selectedEmotion = emotion
                binding.btnStartTimer.isEnabled = true
            }
        }
    }
}