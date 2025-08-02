package com.example.emotionalapp.ui.expression

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityOppositeActionBinding
import com.example.emotionalapp.databinding.PageOppositeAction2RecordBinding // --- 1. 바인딩 클래스 import ---
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class OppositeActionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOppositeActionBinding
    private var currentPage = 0
    private val totalPages = 5

    private val answers = mutableMapOf<String, Any>()
    private var isSaving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOppositeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIndicators(totalPages)
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
            if (isSaving) return@setOnClickListener
            if (!validateAndSaveCurrentPage()) return@setOnClickListener

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
            } else {
                lifecycleScope.launch {
                    isSaving = true
                    binding.navPage.btnNext.isEnabled = false
                    try {
                        saveToFirestore()
                    } catch (e: Exception) {
                        Toast.makeText(this@OppositeActionActivity, "저장에 실패했습니다: ${e.message}", Toast.LENGTH_LONG).show()
                        isSaving = false
                        binding.navPage.btnNext.isEnabled = true
                    }
                }
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            saveCurrentPageData()
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.page_opposite_action_1_guide, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_opposite_action_2_record, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_opposite_action_3_final, binding.pageContainer, false)
            3 -> inflater.inflate(R.layout.page_opposite_action_4_practice, binding.pageContainer, false)
            4 -> inflater.inflate(R.layout.page_opposite_action_5_feedback, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page")
        }
        binding.pageContainer.addView(pageView)

        loadPageContent(pageView)
        updateNavButtons()
        updateIndicators()
    }

    private fun loadPageContent(view: View) {
        when (currentPage) {
            1 -> {
                // --- 2. 바인딩을 사용하여 안전하게 뷰에 접근 ---
                val contentBinding = PageOppositeAction2RecordBinding.bind(view)
                contentBinding.editFeeling.setText(answers["answer1"] as? String ?: "")
                contentBinding.editImpulsiveAction.setText(answers["answer2"] as? String ?: "")
                contentBinding.editOppositeAction.setText(answers["answer3"] as? String ?: "")
            }
            3 -> {
                view.findViewById<CheckBox>(R.id.cb_practice).isChecked = answers["practiced"] as? Boolean ?: false
            }
            4 -> {
                view.findViewById<EditText>(R.id.edit_feedback).setText(answers["answer5"] as? String ?: "")
            }
        }
    }

    private fun saveCurrentPageData() {
        val currentView = binding.pageContainer.getChildAt(0) ?: return
        when (currentPage) {
            1 -> {
                val contentBinding = PageOppositeAction2RecordBinding.bind(currentView)
                answers["answer1"] = contentBinding.editFeeling.text.toString()
                answers["answer2"] = contentBinding.editImpulsiveAction.text.toString()
                answers["answer3"] = contentBinding.editOppositeAction.text.toString()
            }
            3 -> {
                answers["practiced"] = currentView.findViewById<CheckBox>(R.id.cb_practice).isChecked
            }
            4 -> {
                answers["answer5"] = currentView.findViewById<EditText>(R.id.edit_feedback)?.text.toString()
            }
        }
    }

    private fun validateAndSaveCurrentPage(): Boolean {
        saveCurrentPageData()
        return when (currentPage) {
            1 -> {
                if (answers["answer1"].toString().isBlank() || answers["answer2"].toString().isBlank() || answers["answer3"].toString().isBlank()) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show(); false
                } else true
            }
            3 -> {
                if (answers["practiced"] as? Boolean == false) {
                    Toast.makeText(this, "실천에 체크해주세요.", Toast.LENGTH_SHORT).show(); false
                } else true
            }
            4 -> {
                if (answers["answer5"].toString().isBlank()) {
                    Toast.makeText(this, "실천 후 느낌을 입력해주세요.", Toast.LENGTH_SHORT).show(); false
                } else true
            }
            else -> true
        }
    }

    private suspend fun saveToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not logged in")
        val db = FirebaseFirestore.getInstance()
        val timestamp = Timestamp.now()
        val docId = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(timestamp.toDate())

        val data = hashMapOf(
            "answer1" to answers["answer1"],
            "answer2" to answers["answer2"],
            "answer3" to answers["answer3"],
            "practiced" to answers["practiced"],
            "answer5" to answers["answer5"],
            "date" to timestamp
        )

        db.collection("user").document(user.email!!).collection("expressionOpposite").document(docId).set(data).await()
        db.collection("user").document(user.email!!).update("countComplete.opposite", FieldValue.increment(1)).await()

        Toast.makeText(this, "훈련 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AllTrainingPageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun setupIndicators(count: Int) {
        val indicatorContainer = binding.navPage.indicatorContainer
        indicatorContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply { setMargins(8, 0, 8, 0) }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updateIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray)
        }
    }

    private fun updateNavButtons() {
        binding.navPage.btnPrev.isEnabled = currentPage > 0
        binding.navPage.btnNext.text = if (currentPage == totalPages - 1) "완료" else "다음 →"
    }
}