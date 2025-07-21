package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityEmotionFeedbackBinding
import com.example.emotionalapp.ui.alltraining.ExpressionActivity

class EmotionFeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            returnToTrainingList()
        }
    }

    private fun returnToTrainingList() {
        val intent = Intent(this, ExpressionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
}