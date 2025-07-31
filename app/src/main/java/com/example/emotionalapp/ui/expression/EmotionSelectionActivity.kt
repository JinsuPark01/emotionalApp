package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View // --- 이 줄을 추가했습니다 ---
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityEmotionSelectionBinding

class EmotionSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionSelectionBinding
    private var selectedEmotion: String? = null
    private var selectedView: View? = null

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
                val selectedDurationInMillis = when (binding.rgTimerDuration.checkedRadioButtonId) {
                    R.id.rb_1_min -> 60 * 1000L
                    R.id.rb_3_min -> 180 * 1000L
                    else -> 120 * 1000L
                }

                val intent = Intent(this, EmotionTimerActivity::class.java).apply {
                    putExtra("SELECTED_EMOTION", it)
                    putExtra("TIMER_DURATION", selectedDurationInMillis)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupEmotionGrid() {
        val emotions = mapOf(
            "행복" to R.drawable.emotion_happy,
            "즐거움" to R.drawable.emotion_joy,
            "자신감" to R.drawable.emotion_confident,
            "슬픔" to R.drawable.emotion_sad,
            "두려움" to R.drawable.emotion_fear,
            "당황" to R.drawable.emotion_embarrassed,
            "걱정" to R.drawable.emotion_anxious,
            "짜증" to R.drawable.emotion_annoyed,
            "분노" to R.drawable.emotion_angry
        )

        for ((emotionText, iconResId) in emotions) {
            val emotionView = createEmotionView(emotionText, iconResId)
            binding.gridEmotions.addView(emotionView)
        }
    }

    private fun createEmotionView(emotion: String, iconResId: Int): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_emotion_card, binding.gridEmotions, false)

        val imageView = view.findViewById<ImageView>(R.id.iv_emotion)
        val textView = view.findViewById<TextView>(R.id.tv_emotion)

        imageView.setImageResource(iconResId)
        textView.text = emotion

        val params = android.widget.GridLayout.LayoutParams().apply {
            width = 0
            height = android.widget.GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
            setMargins(8, 8, 8, 8)
        }
        view.layoutParams = params

        view.setOnClickListener {
            selectedView?.let {
                it.background = ContextCompat.getDrawable(this, R.drawable.bg_topic_button)
                it.findViewById<TextView>(R.id.tv_emotion).setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }

            it.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
                setTint(ContextCompat.getColor(this@EmotionSelectionActivity, R.color.purple_500))
            }
            (it as LinearLayout).findViewById<TextView>(R.id.tv_emotion).setTextColor(ContextCompat.getColor(this, android.R.color.white))

            selectedView = it
            selectedEmotion = emotion
            binding.btnStartTimer.isEnabled = true
        }
        return view
    }
}