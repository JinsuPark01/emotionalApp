package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.example.emotionalapp.data.DrivenActionQuizItem
import com.example.emotionalapp.databinding.ActivityDrivenActionQuizBinding
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity

class DrivenActionQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivenActionQuizBinding
    private lateinit var quizItems: List<DrivenActionQuizItem>
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivenActionQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadQuizData()
        setupListeners()
        displayQuestion()
    }

    private fun loadQuizData() {
        quizItems = listOf(
            DrivenActionQuizItem(
                1,
                R.string.quiz_driven_q1_question,
                false,
                R.string.quiz_driven_q1_correct_feedback,
                R.string.quiz_driven_q1_wrong_feedback
            ),
            DrivenActionQuizItem(
                2,
                R.string.quiz_driven_q2_question,
                true,
                R.string.quiz_driven_q2_correct_feedback,
                R.string.quiz_driven_q2_wrong_feedback
            ),
            DrivenActionQuizItem(
                3,
                R.string.quiz_driven_q3_question,
                false,
                R.string.quiz_driven_q3_correct_feedback,
                R.string.quiz_driven_q3_wrong_feedback
            )
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
                showCompletionDialog()
            }
        }
    }

    // --- 새로운 함수 추가 ---
    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("훈련 완료!")
            .setMessage("감정을 마주하고, 스스로 선택하는 힘을 길러봤어요. 감정에 휘둘리기보다, 그 감정을 이해하고 주도하려는 시도가 지금의 변화를 만들었어요. 내 감정의 주인이 되기 위한 여정은 이제 시작이에요. 지금처럼 차분하게, 계속 연습해 나가요!")
            .setPositiveButton("확인") { _, _ ->
                // '확인' 버튼을 누르면 액티비티를 종료합니다.
                startActivity(Intent(this@DrivenActionQuizActivity, AllTrainingPageActivity::class.java))
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
            // --- 여기가 핵심 수정 부분입니다 ---
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