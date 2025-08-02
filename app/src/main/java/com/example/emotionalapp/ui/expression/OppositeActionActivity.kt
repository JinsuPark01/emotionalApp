package com.example.emotionalapp.ui.expression

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityOppositeActionBinding
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class OppositeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOppositeActionBinding
    private var currentPage = 0
    private val totalPages = 3

    private val answers = mutableMapOf<String, String>()
    // --- 1. 중복 저장을 방지하기 위한 상태 플래그 추가 ---
    private var isSaving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOppositeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIndicators()
        updatePage()

        binding.btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        binding.navPage.btnNext.setOnClickListener {
            // --- 2. 코루틴을 사용하여 저장 로직 호출 ---
            if (isSaving) return@setOnClickListener // 이미 저장 중이면 무시

            if (!validateCurrentPage()) return@setOnClickListener
            saveCurrentInput()

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                // lifecycleScope를 사용하여 코루틴 시작
                lifecycleScope.launch {
                    isSaving = true // 저장 시작
                    binding.navPage.btnNext.isEnabled = false // 버튼 비활성화
                    try {
                        saveToFirestoreSuspend() // suspend 함수 호출
                        Toast.makeText(this@OppositeActionActivity, "훈련 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@OppositeActionActivity, AllTrainingPageActivity::class.java)
                        startActivity(intent)
                        finish()

                    } catch (e: Exception) {
                        Toast.makeText(this@OppositeActionActivity, "저장에 실패했습니다: ${e.message}", Toast.LENGTH_LONG).show()
                        isSaving = false // 실패 시 다시 저장 가능하도록 상태 복원
                        binding.navPage.btnNext.isEnabled = true // 버튼 다시 활성화
                    }
                }
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            saveCurrentInput()
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }
    }

    // --- 3. Firestore 저장 함수를 suspend 함수로 변경 ---
    private suspend fun saveToFirestoreSuspend() {
        val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not logged in")
        val db = FirebaseFirestore.getInstance()
        val timestamp = Timestamp.now()

        val docId = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.format(timestamp.toDate())

        val data = hashMapOf(
            "answer1" to answers["answer1"],
            "answer2" to answers["answer2"],
            "answer3" to answers["answer3"],
            "answer5" to answers["answer5"],
            "date" to timestamp
        )

        // suspendCancellableCoroutine을 사용하여 콜백 기반 API를 suspend 함수로 변환
        // 또는 kotlinx-coroutines-play-services 라이브러리의 await() 확장 함수 사용
        db.collection("user").document(user.email ?: "unknown_user")
            .collection("expressionOpposite")
            .document(docId)
            .set(data)
            .await() // Firestore 작업이 끝날 때까지 여기서 실행이 '일시정지' 됩니다.

        db.collection("user")
            .document(user.email ?: "unknown_user")
            .update("countComplete.opposite", FieldValue.increment(1))
            .await()
    }

    // ... (나머지 함수들은 변경 없습니다) ...
    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_opposite_action_1_guide, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_opposite_action_2_record, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_opposite_action_3_final, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page number")
        }
        binding.pageContainer.addView(pageView)

        loadPageContent(pageView)
        updateNavButtons()
        updateIndicators()
    }
    private fun loadPageContent(view: View) {
        if (currentPage == 1) {
            view.findViewById<EditText>(R.id.answer1).setText(answers["answer1"])
            view.findViewById<EditText>(R.id.answer2).setText(answers["answer2"])
            view.findViewById<EditText>(R.id.answer3).setText(answers["answer3"])
            view.findViewById<EditText>(R.id.answer5).setText(answers["answer5"])
        }
    }
    private fun validateCurrentPage(): Boolean {
        val currentView = binding.pageContainer.getChildAt(0) ?: return true
        if (currentPage == 1) {
            val answer1 = currentView.findViewById<EditText>(R.id.answer1)?.text.toString().trim()
            val answer2 = currentView.findViewById<EditText>(R.id.answer2)?.text.toString().trim()
            val answer3 = currentView.findViewById<EditText>(R.id.answer3)?.text.toString().trim()
            val answer5 = currentView.findViewById<EditText>(R.id.answer5)?.text.toString().trim()
            if (answer1.isBlank() || answer2.isBlank() || answer3.isBlank() || answer5.isBlank()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
    private fun saveCurrentInput() {
        val currentView = binding.pageContainer.getChildAt(0) ?: return
        if (currentPage == 1) {
            answers["answer1"] = currentView.findViewById<EditText>(R.id.answer1)?.text.toString()
            answers["answer2"] = currentView.findViewById<EditText>(R.id.answer2)?.text.toString()
            answers["answer3"] = currentView.findViewById<EditText>(R.id.answer3)?.text.toString()
            answers["answer5"] = currentView.findViewById<EditText>(R.id.answer3)?.text.toString()

        }
    }
    private fun setupIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        indicatorContainer.removeAllViews()
        for (i in 0 until totalPages) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    setMargins(8, 0, 8, 0)
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }
    private fun updateIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black
                else R.drawable.ic_dot_circle_gray
            )
        }
    }
    private fun updateNavButtons() {
        binding.navPage.btnPrev.isEnabled = currentPage > 0
        binding.navPage.btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"
    }
}