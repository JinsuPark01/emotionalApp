package com.example.emotionalapp.ui.emotion

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.data.EmotionAvoidanceQuizItem
import com.example.emotionalapp.databinding.ActivityEmotionAvoidanceQuizBinding

class EmotionAvoidanceQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionAvoidanceQuizBinding
    private lateinit var quizItems: List<EmotionAvoidanceQuizItem>
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionAvoidanceQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadQuizData()
        setupListeners()
        displayQuestion()
        setupTabs()
    }

    private fun setupTabs() {
        binding.tabRecord.visibility = View.GONE
        binding.underlineRecord.visibility = View.GONE
        binding.tabPracticeContainer.layoutParams = (binding.tabPracticeContainer.layoutParams as LinearLayout.LayoutParams).apply {
            width = 0
            weight = 2f
        }
    }

    private fun loadQuizData() {
        quizItems = listOf(
            EmotionAvoidanceQuizItem(1, R.string.quiz_q1_question, false, R.string.quiz_q1_correct_feedback, R.string.quiz_q1_wrong_feedback),
            EmotionAvoidanceQuizItem(2, R.string.quiz_q2_question, false, R.string.quiz_q2_correct_feedback, R.string.quiz_q2_wrong_feedback),
            EmotionAvoidanceQuizItem(3, R.string.quiz_q3_question, true, R.string.quiz_q3_correct_feedback, R.string.quiz_q3_wrong_feedback)
        )
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnO.setOnClickListener { checkAnswer(true) }
        binding.btnX.setOnClickListener { checkAnswer(false) }
        binding.btnNextQuestion.setOnClickListener {
            currentQuestionIndex++
            if (currentQuestionIndex < quizItems.size) {
                displayQuestion()
            } else {
                finish()
            }
        }
    }

    private fun displayQuestion() {
        val currentQuestion = quizItems[currentQuestionIndex]
        binding.tvQuestion.text = getString(currentQuestion.questionResId)
        resetUiForNewQuestion()
    }

    private fun checkAnswer(selectedAnswer: Boolean) {
        val currentQuestion = quizItems[currentQuestionIndex]
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer
        binding.btnO.isEnabled = false
        binding.btnX.isEnabled = false
        showFeedback(isCorrect)
    }

    private fun showFeedback(isCorrect: Boolean) {
        val currentQuestion = quizItems[currentQuestionIndex]
        binding.cardFeedback.visibility = View.VISIBLE
        binding.navContainer.visibility = View.VISIBLE

        if (isCorrect) {
            binding.tvFeedbackTitle.text = "정답입니다!"
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.purple_700))
            binding.tvFeedbackContent.text = getString(currentQuestion.correctFeedbackResId)
        } else {
            binding.tvFeedbackTitle.text = "오답입니다"
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_error))
            binding.tvFeedbackContent.text = getString(currentQuestion.wrongFeedbackResId)
        }

        if (currentQuestionIndex == quizItems.size - 1) {
            binding.btnNextQuestion.text = "완료"
        } else {
            binding.btnNextQuestion.text = "다음 문제"
        }
    }

    private fun resetUiForNewQuestion() {
        binding.cardFeedback.visibility = View.GONE
        binding.navContainer.visibility = View.GONE
        binding.btnO.isEnabled = true
        binding.btnX.isEnabled = true
    }
}