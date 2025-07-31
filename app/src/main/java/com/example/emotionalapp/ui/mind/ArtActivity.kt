package com.example.emotionalapp.ui.mind

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import java.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ArtActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val totalPages = 9
    private var currentPage = 0

    private var selectedImages = mutableListOf<Int>()
    private var selectedImageIndices = mutableListOf<Int>()
    private var selectedImageResourceIds = mutableListOf<Int>()

    private val imageResIds = arrayOf(
        R.drawable.art1, R.drawable.art2, R.drawable.art3,
        R.drawable.art4, R.drawable.art5, R.drawable.art6,
        R.drawable.art7, R.drawable.art8, R.drawable.art9,
        R.drawable.art10, R.drawable.art11, R.drawable.art12
    )

    private val userAnswers = arrayOf(
        MutableList(5) { "" }, // 첫 번째 이미지의 5문항
        MutableList(5) { "" }  // 두 번째 이미지의 5문항
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ ->
                    finish()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

        setupIndicators(totalPages)
        updatePage()

        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePage()
            }
        }

        btnNext.setOnClickListener {
            btnNext.isEnabled = false

            if (currentPage == 2) {
                if (selectedImages.size < 2) {
                    Toast.makeText(this, "이미지를 2개 선택해야 합니다.", Toast.LENGTH_SHORT).show()
                    btnNext.isEnabled = true
                    return@setOnClickListener
                }
                selectedImageIndices.clear()
                selectedImageIndices.addAll(selectedImages)
                selectedImageResourceIds.clear()
                selectedImageIndices.forEach { index ->
                    if (index in imageResIds.indices) {
                        selectedImageResourceIds.add(imageResIds[index])
                    }
                }
            }

            if (currentPage in 3..8) {
                val pageView = pageContainer.getChildAt(0)
                val answer1 = pageView?.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView?.findViewById<EditText>(R.id.answer2)

                val isSingleAnswerPage = (currentPage - 3) % 3 == 2
                val isValid = if (isSingleAnswerPage) {
                    answer1?.text.toString().isNotBlank() == true
                } else {
                    answer1?.text.toString().isNotBlank() == true && answer2?.text.toString().isNotBlank() == true
                }

                if (!isValid) {
                    Toast.makeText(this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show()
                    btnNext.isEnabled = true
                    return@setOnClickListener
                }

                val imageIndex = if (currentPage in 3..5) 0 else 1
                val pageIndex = (currentPage - 3) % 3
                val startIndex = pageIndex * 2
                val answers = mutableListOf<String>()
                answer1?.text.toString().let { answers.add(it) }
                if (!isSingleAnswerPage) answer2?.text.toString().let { answers.add(it) }

                while (userAnswers[imageIndex].size < startIndex + answers.size) {
                    userAnswers[imageIndex].add("")
                }
                for (i in answers.indices) {
                    userAnswers[imageIndex][startIndex + i] = answers[i]
                }
            }

            if (currentPage < totalPages - 1) {
                currentPage++
                updatePage()
                btnNext.isEnabled = true
            } else {
                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        saveToDatabaseSuspend()
                    }

                    if (success) {
                        AlertDialog.Builder(this@ArtActivity)
                            .setTitle("훌륭합니다!")
                            .setMessage("다른 시각에서 상황을 바라보는 연습은 처음엔 조금 어색할 수 있어요. 하지만 꾸준히 해보면 점차 익숙해져 나중에는 자연스럽게 여러 관점으로 생각할 수 있게 됩니다.\n\n이 연습에서 정해진 정답은 없다는 점을 기억하세요. 우리가 이렇게 다양한 시각을 연습하는 이유는 '더 맞는', ‘옳은’ 해석을 찾기 위해서가 아닙니다. 단지 우리가 처음에 떠올린 생각 외에도 다른 해석이 얼마든지 가능하다는 사실을 알아내기 위함입니다.")
                            .setPositiveButton("확인") { _, _ ->
                                startActivity(Intent(this@ArtActivity, AllTrainingPageActivity::class.java))
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    } else {
                        Toast.makeText(this@ArtActivity, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        btnNext.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        repeat(count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updatePage() {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = if (currentPage == 0) "인지적 평가" else "모호한 그림 연습하기"

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_art_traning_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_art_traning_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_art_traning_2, pageContainer, false)
            in 3..8 -> inflater.inflate(R.layout.fragment_mind_art_traning_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_art_traning_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (currentPage) {
            1 -> {
                val image = pageView.findViewById<ImageView>(R.id.art0)
                image?.setImageResource(R.drawable.art0)

                image?.setOnClickListener {
                    showZoomDialog(R.drawable.art0)
                }
            }

            2 -> {
                val imageViews = listOf(
                    pageView.findViewById<ImageView>(R.id.art1),
                    pageView.findViewById<ImageView>(R.id.art2),
                    pageView.findViewById<ImageView>(R.id.art3),
                    pageView.findViewById<ImageView>(R.id.art4),
                    pageView.findViewById<ImageView>(R.id.art5),
                    pageView.findViewById<ImageView>(R.id.art6),
                    pageView.findViewById<ImageView>(R.id.art7),
                    pageView.findViewById<ImageView>(R.id.art8),
                    pageView.findViewById<ImageView>(R.id.art9),
                    pageView.findViewById<ImageView>(R.id.art10),
                    pageView.findViewById<ImageView>(R.id.art11),
                    pageView.findViewById<ImageView>(R.id.art12)
                )

                fun updateImageUI() {
                    imageViews.forEachIndexed { index, imageView ->
                        if (selectedImages.contains(index)) {
                            imageView?.scaleX = 0.9f
                            imageView?.scaleY = 0.9f
                            imageView?.background = null
                            imageView?.setPadding(0, 0, 0, 0)
                        } else {
                            imageView?.scaleX = 1.0f
                            imageView?.scaleY = 1.0f
                            imageView?.background = null
                            imageView?.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                imageViews.forEachIndexed { index, imageView ->
                    imageView?.setOnClickListener {
                        if (selectedImages.contains(index)) {
                            selectedImages.remove(index)
                        } else {
                            if (selectedImages.size >= 2) {
                                Toast.makeText(this, "2개까지만 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            selectedImages.add(index)
                        }
                        updateImageUI()
                    }
                }

                updateImageUI()
            }

            in 3..8 -> {
                val question1 = pageView.findViewById<TextView>(R.id.question1)
                val question2 = pageView.findViewById<TextView>(R.id.question2)
                val answer1 = pageView.findViewById<EditText>(R.id.answer1)
                val answer2 = pageView.findViewById<EditText>(R.id.answer2)
                val imageView = pageView.findViewById<ImageView>(R.id.image)

                val isFirstImagePhase = currentPage in 3..5
                val imageIndex = if (isFirstImagePhase) 0 else 1
                val pageIndex = (currentPage - 3) % 3
                val startIndex = pageIndex * 2

                val imageResId = selectedImageResourceIds.getOrNull(imageIndex) ?: 0
                imageView?.setImageResource(imageResId)
                imageView?.visibility = View.VISIBLE

                val questions = listOf(
                    "상황 1. 마음이 지치고 불안한 날\n예) 부모님과 다툰 후 / 친구와의 갈등 / 직장에서 큰 실수를 한 날\n\n1. 그림의 장면이 어떤 상황처럼 느껴지나요?",
                    "1-1. 이 상황에서 어떤 감정이 느껴지나요?",
                    "상황 2. 기분이 좋은 날\n예) 친구들과 즐거운 시간을 보낸 후 / 좋은 소식을 들은 날 / 따뜻한 날씨를 즐기는 날\n\n2. 같은 그림을 봤을 때, 어떤 상황으로 보이나요?",
                    "2-1. 이 상황에서 느껴지는 감정은 무엇인가요?",
                    "3. 같은 그림인데도 상황에 따라 다르게 느껴졌나요? 나의 감정 상태가 해석에 어떤 영향을 준 것 같나요?"
                )

                when (pageIndex) {
                    0 -> {
                        question1?.text = questions[0]
                        question2?.text = questions[1]
                        question2?.visibility = View.VISIBLE
                        answer2?.visibility = View.VISIBLE
                    }
                    1 -> {
                        question1?.text = questions[2]
                        question2?.text = questions[3]
                        question2?.visibility = View.VISIBLE
                        answer2?.visibility = View.VISIBLE
                    }
                    2 -> {
                        question1?.text = questions[4]
                        question2?.visibility = View.GONE
                        answer2?.visibility = View.GONE
                    }
                }

                val savedAnswers = userAnswers[imageIndex]
                if (savedAnswers.size > startIndex) {
                    answer1?.setText(savedAnswers[startIndex])
                }
                if (savedAnswers.size > startIndex + 1 && pageIndex != 2) {
                    answer2?.setText(savedAnswers[startIndex + 1])
                }

                imageView?.setOnClickListener {
                    if (imageResId != 0) {
                        showZoomDialog(imageResId)
                    }
                }
            }
        }

        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0)
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        else
            ColorStateList.valueOf(Color.parseColor("#3CB371"))

        btnNext.text = if (currentPage == totalPages - 1) "완료 →" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
            )
        }
    }

    private fun showZoomDialog(imageResId: Int) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val view = layoutInflater.inflate(R.layout.dialog_zoom_image, null)
        val imageView = view.findViewById<ImageView>(R.id.zoomImage)

        Glide.with(this)
            .load(imageResId)
            .into(imageView)

        view.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun getImageNameByResId(resId: Int): String {
        return when(resId) {
            R.drawable.art1 -> "art1"
            R.drawable.art2 -> "art2"
            R.drawable.art3 -> "art3"
            R.drawable.art4 -> "art4"
            R.drawable.art5 -> "art5"
            R.drawable.art6 -> "art6"
            R.drawable.art7 -> "art7"
            R.drawable.art8 -> "art8"
            R.drawable.art9 -> "art9"
            R.drawable.art10 -> "art10"
            R.drawable.art11 -> "art11"
            R.drawable.art12 -> "art12"
            else -> ""
        }
    }

    private suspend fun saveToDatabaseSuspend(): Boolean = suspendCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        if (user == null || userEmail == null) {
            continuation.resume(false)
            return@suspendCoroutine
        }

        val db = FirebaseFirestore.getInstance()
        val timestamp = Timestamp.now()

        val firstImageName = getImageNameByResId(selectedImageResourceIds.getOrNull(0) ?: 0)
        val secondImageName = getImageNameByResId(selectedImageResourceIds.getOrNull(1) ?: 0)

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val docId = sdf.format(timestamp.toDate())

        val data = hashMapOf<String, Any>(
            "firstImage" to firstImageName,
            "secondImage" to secondImageName,
            "date" to timestamp
        )

        userAnswers[0].forEachIndexed { index, answer ->
            data["1art_${index + 1}"] = answer
        }

        userAnswers[1].forEachIndexed { index, answer ->
            data["2art_${index + 1}"] = answer
        }

        db.collection("user").document(userEmail)
            .collection("mindArt").document(docId)
            .set(data)
            .addOnSuccessListener {
                db.collection("user").document(userEmail)
                    .update("countComplete.art", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Log.d("Firestore", "카운트 증가 성공")
                        continuation.resume(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "카운트 증가 실패", e)
                        continuation.resume(false)
                    }
            }
            .addOnFailureListener {
                continuation.resume(false)
            }
    }
}