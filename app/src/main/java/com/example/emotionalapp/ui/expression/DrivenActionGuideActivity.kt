package com.example.emotionalapp.ui.expression

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityDrivenActionGuideBinding

class DrivenActionGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivenActionGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivenActionGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // --- 여기가 핵심 수정 부분입니다 ---
        // '퀴즈 풀어보기' 버튼을 누르면 퀴즈로 가는 대신 팝업을 띄웁니다.
        binding.btnStartQuiz.setOnClickListener {
            showCompletionDialog()
        }

        binding.guideScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val scrollView = v as ScrollView
            val childView = scrollView.getChildAt(0)

            if (childView != null) {
                val diff = (childView.bottom - (scrollView.height + scrollView.scrollY))
                if (diff <= 0) {
                    // 스크롤이 맨 아래에 닿으면 버튼의 텍스트를 '교육 완료'로 변경
                    binding.btnStartQuiz.text = "교육 완료"
                    binding.btnStartQuiz.visibility = View.VISIBLE
                } else {
                    binding.btnStartQuiz.visibility = View.GONE
                }
            }
        }
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("교육 완료!")
            .setMessage("정서-주도 행동의 의미를 탐색하고, 스스로의 행동을 돌아봤어요. 이제 감정에 휘둘리는 대신 감정과 함께 걷는 연습이 시작됐어요. 지금처럼 차근차근, 계속 나아가 보아요!")
            .setPositiveButton("확인") { dialog, which ->
                // '확인' 버튼을 누르면 교육 화면을 종료합니다.
                finish()
            }
            .setCancelable(false)
            .show()
    }
}