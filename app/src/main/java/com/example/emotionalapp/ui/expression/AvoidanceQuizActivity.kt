package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.data.EmotionAvoidanceQuizItem
import com.example.emotionalapp.databinding.ActivityEmotionAvoidanceQuizBinding
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class AvoidanceQuizActivity : AppCompatActivity() {

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
    }

    private fun loadQuizData() {
        quizItems = listOf(
            EmotionAvoidanceQuizItem(
                1,
                R.string.quiz_q1_question,
                false,
                R.string.quiz_q1_correct_feedback,
                R.string.quiz_q1_wrong_feedback
            ),
            EmotionAvoidanceQuizItem(
                2,
                R.string.quiz_q2_question,
                false,
                R.string.quiz_q2_correct_feedback,
                R.string.quiz_q2_wrong_feedback
            ),
            EmotionAvoidanceQuizItem(
                3,
                R.string.quiz_q3_question,
                true,
                R.string.quiz_q3_correct_feedback,
                R.string.quiz_q3_wrong_feedback
            )
        )
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnO.setOnClickListener {
            checkAnswer(true)
        }

        binding.btnX.setOnClickListener {
            checkAnswer(false)
        }

        // --- 여기가 핵심 수정 부분입니다 ---
        binding.btnNextQuestion.setOnClickListener {
            currentQuestionIndex++
            if (currentQuestionIndex < quizItems.size) {
                displayQuestion()
            } else {
                // 마지막 문제 완료 시, 팝업을 띄웁니다.
                showCompletionDialog()
            }
        }
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("훈련 완료!")
            .setMessage("감정을 구분하고, 회피의 효과와 한계를 이해 해봤어요. 단기적 편안함에 머무르지 않고, 장기적인 회복을 위한 첫걸음을 내디딘 결과, 회피를 넘어 감정과 함께 살아가는 연습이 시작됐어요. 지금처럼 차근차근, 계속 나아가 보아요!")
            .setPositiveButton("확인") { _, _ ->
                // '확인' 버튼을 누르면 액티비티를 종료합니다.
                startActivity(Intent(this@AvoidanceQuizActivity, AllTrainingPageActivity::class.java))
                finish()
            }
            .setCancelable(false) // 팝업 바깥을 눌러도 닫히지 않게 설정
            .show()
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
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.error_red))
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