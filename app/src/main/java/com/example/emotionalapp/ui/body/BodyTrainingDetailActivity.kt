package com.example.emotionalapp.ui.body

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

class BodyTrainingDetailActivity : AppCompatActivity() {

    private lateinit var trainingId: String
    private lateinit var prefs: SharedPreferences

    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View
    private lateinit var layoutPractice: View
    private lateinit var layoutRecord: View
    private lateinit var practiceContent: TextView
    private lateinit var btnStartPractice: Button
    private lateinit var btnDelete: Button

    // 기록보기 전용 뷰
    private lateinit var tvEmpty: TextView
    private lateinit var scrollRec: ScrollView
    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_detail)

        // Intent 및 SharedPreferences 초기화
        trainingId = intent.getStringExtra("TRAINING_ID") ?: ""
        prefs = getSharedPreferences("body_training_records", Context.MODE_PRIVATE)

        // 상단 바 설정
        findViewById<TextView>(R.id.tv_page_title).text =
            intent.getStringExtra("TRAINING_TITLE") ?: "훈련"
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // 뷰 바인딩
        tabPractice       = findViewById(R.id.tabPractice)
        tabRecord         = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord   = findViewById(R.id.underlineRecord)
        layoutPractice    = findViewById(R.id.layoutPractice)
        layoutRecord      = findViewById(R.id.layoutRecord)
        practiceContent   = findViewById(R.id.tvPracticeContent)
        btnStartPractice  = findViewById(R.id.btnStartPractice)
        btnDelete         = findViewById(R.id.btnDeleteRecord)


        // 연습 시작 버튼 클릭 리스너
        btnStartPractice.setOnClickListener {
            // Intent로 TRAINING_ID 전달 후 Practice 화면으로 이동
            Intent(this, BodyTrainingPracticeActivity::class.java).apply {
                putExtra("TRAINING_ID", trainingId)       // 이미 초기화한 trainingId 사용
                putExtra("TRAINING_TITLE",
                    findViewById<TextView>(R.id.tv_page_title).text.toString()
                )
            }.also { startActivity(it) }
        }


        tvEmpty   = findViewById(R.id.tvEmptyRecord)
        scrollRec = findViewById(R.id.scrollRecordContent)
        tvTitle   = findViewById(R.id.tvRecordTitle)
        tvContent = findViewById(R.id.tvRecordContent)

        // Practice 콘텐츠 설정
        val practiceText = when (trainingId) {
            "bt_detail_002" -> """
                DAY 1 - 내 몸에 귀기울이기
                
                목표: 판단 없이 몸 전체 감각을 고르게 인식하기
                
                이렇게 진행돼요.
                - 총 6분간 진행됩니다.
                - 머리부터 발끝까지 천천히 살펴봅니다.
                - 지금 이 순간, 그저 느껴보기만 해보세요.
                
                [훈련 전 도움말]
                - 오늘은 눈을 감고 머리부터 발끝까지 몸 전체를 살펴볼 거예요.
                  마치 스캔하듯이요.
                - 몸의 무게감, 따뜻함, 긴장된 부위, 닿는 느낌을 판단하지 않고
                  그대로 느껴보는 연습입니다.
                - 잘하려고 애쓰지 않아도 괜찮아요. 느껴지는 그대로,
                  그 자리에 머무는 것이 목적이에요.

            """.trimIndent()

            "bt_detail_003" -> """
                DAY 2 - 먹기명상(음식의 오감 알아차리기)
                
                목표: 한 입의 음식을 천천히 먹으며, 오감과 감정을 인식하기
                
                [이렇게 진행돼요]
                - 배가 너무 고프지 않은 상태에서 참여해 주세요.
                - 너무 많은 양 말고, 한 입만 준비해주세요.
                - 어색하거나 집중이 안 되어도 괜찮아요.
                
                * 먹기 명상에 좋은 음식
                - 건포도, 견과류, 말린 과일, 초콜릿 한 조각, 귤, 딸기 등
                  작은 과일과 같은 음식
                  
                * 먹기 명상에 피해야할 음식
                국물음식, 너무 맵거나 짠 음식, 비빔밥, 잡채 같은 복합음식
            """.trimIndent()


            "bt_detail_004" -> """
                DAY 3 - 감정과 몸 연결하기

                목표: 감정을 떠올리고, 그때 몸의 반응을 부드럽게 느껴보기
                
                “ 감정은 생각보다 먼저 몸에 나타납니다.”
                
                [훈련 전 도움말]
                - 오늘은 최근의 감정을 떠올리고, 그 감정이 몸 어디에
                  어떻게 나타나는지 알아차려볼거에요.
                - 감정을 억누르거나 바꾸려 하지 않고, 그 느낌이 몸에서 어떤
                  감각으로 있는지 조용히 느껴봅니다.
                - 어렵지 않아요. ‘그냥 불편한 곳이 어디지?’, ‘어떤 느낌이지?‘
                  하고 관찰하는 연습입니다.
            """.trimIndent()

            "bt_detail_005" -> """
            DAY 4 - 특정 감각에 집중하기

            목표: 특정 감각에 관심을 집중하며 주의산만 회복을 연습하기

            [감각 집중 훈련을 위한 도움말]
            1. 호흡의 움직임
            - 숨을 들이쉴 때, 배나 가슴이 살짝 올라오는 느낌을 느껴보세요.
            - 코끝을 지나는 공기의 흐름이느껴질 수도 있어요.

            2. 심장박동
            - 가슴 안쪽에서 쿵쿵 뛰는 느낌이 있을 수 있어요.
            - 손이나 귀에 집중하면 미세한 박동이 더 느껴질 수 있어요.

            3. 발바닥의 무게감
            - 바닥에 닿아 있는 발바닥이 어떻게 눌리고 있는지 느껴보세요.
            - 무게감이나 따뜻함, 약한 압박이 있을 수 있어요.
            """.trimIndent()

            "bt_detail_006" -> """
            DAY 5 - 기본적인 바디스캔(감각 알아차리기)

            목표: 몸의 각 부위에 주의를 이동시키며, “ 지금 이 부위에서
            어떤 감각이 느껴지는지" 알아차리기

            [이렇게 진행돼요]
            - 머리부터 발끝까지 감각을 따라갑니다.
            - 감각이 없어도 괜찮고, 판단하지 않고 관찰하는 것이 중요해요.
            - “잘하려고 하지 않아도괜찮아요”
            """.trimIndent()

            "bt_detail_007" -> """
            DAY 6 - 바디스캔(감각의 변화 알아차리기)

            목표: 바디스캔 훈련을 하며, 감각이 처음보다 어떻게 달라졌는지 느끼기

            [이렇게 진행돼요]
            - 어제와 비슷한 흐름으로 진행되지만, 조금 더 익숙하게
            느껴질 수 있어요.
            - 잘하려고 하지 않아도 괜찮아요
            - 그저 '지금 여기'의 몸을 있는 그대로 관찰합니다.

            [마음가짐]
            - 감각이 느껴지지 않아도 괜찮아요
            - 중요한 건' 느끼려는 태도' 그 자체 입니다.
            - 생각이 떠오르면 생각이 '떠올랐구나' 하고 다시 돌아오면 됩니다.
            """.trimIndent()

            "bt_detail_008" -> """
                DAY 7 - 먹기명상(먹기명상을 통한 감정과 신체 연결 알아차림)
                
                목표: 감정이 먹는 행동에 어떤 영향을 주는지 알아차리고,
                     반응 관찰하기
                
                [이렇게 진행돼요]
                ①. 오늘 먹고 싶은 음식 하나 떠올려보세요.
                ②. ‘왜' 먹고 싶은지 자문해봅니다.
                ③. 한 입 먹기 전, 감정을 살펴보고,
                ④. 한 입 먹은 후, 감정의 변화를 알아차려봅니다.
                ⑤. 떠오르는 감정에 이름 붙이지 않고 흘려보냅니다.
                
                [유의점]
                - 감정이 떠오를 수 있어요.
                 → 괜찮습니다. 판단하지 않고, 그냥 느껴보세요.
                - '왜' 먹고 싶은가를 억지로 분석하지 않아도 됩니다.
            """.trimIndent()

            else -> "준비 중인 연습입니다."
        }
        practiceContent.text = practiceText

        // 연습 시작 버튼
        btnStartPractice.setOnClickListener {
            Intent(this, BodyTrainingPracticeActivity::class.java).apply {
                putExtra("TRAINING_ID", trainingId)
                putExtra(
                    "TRAINING_TITLE",
                    findViewById<TextView>(R.id.tv_page_title).text.toString()
                )
            }.also { startActivity(it) }
        }

        // 삭제 버튼 리스너
        btnDelete.setOnClickListener {
            prefs.edit().remove("feedback_$trainingId").apply()
            Toast.makeText(this, "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            showRecordUI(null)
        }

        // 탭 리스너 및 초기 탭
        tabPractice.setOnClickListener { selectTab(true) }
        tabRecord.setOnClickListener {
            selectTab(false)
            showRecordUI(prefs.getString("feedback_$trainingId", null))
        }
        selectTab(true)
    }

    override fun onResume() {
        super.onResume()
        if (layoutRecord.isShown) {
            showRecordUI(prefs.getString("feedback_$trainingId", null))
        }
    }

    private fun showRecordUI(feedback: String?) {
        if (feedback.isNullOrBlank()) {
            tvEmpty.visibility   = View.VISIBLE
            scrollRec.visibility = View.GONE
        } else {
            tvEmpty.visibility   = View.GONE
            scrollRec.visibility = View.VISIBLE
            tvTitle.text   = "${getUserDisplayName()}님의 소감입니다."
            tvContent.text = feedback
        }
    }

    //이후 실제 사용자명 가져오는 로직으로 교체할 것
    private fun getUserDisplayName(): String = "USER"

    private fun selectTab(practice: Boolean) {
        tabPractice.setTextColor(
            resources.getColor(if (practice) R.color.black else R.color.gray, null)
        )
        tabRecord.setTextColor(
            resources.getColor(if (practice) R.color.gray else R.color.black, null)
        )
        underlinePractice.visibility = if (practice) View.VISIBLE else View.GONE
        underlineRecord.visibility   = if (practice) View.GONE else View.VISIBLE
        layoutPractice.visibility    = if (practice) View.VISIBLE else View.GONE
        layoutRecord.visibility      = if (practice) View.GONE else View.VISIBLE
    }
}
