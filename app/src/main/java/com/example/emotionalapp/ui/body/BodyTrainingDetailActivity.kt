package com.example.emotionalapp.ui.body

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R

class BodyTrainingDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_detail)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvTitle = findViewById<TextView>(R.id.tv_page_title)
        val tvContent = findViewById<TextView>(R.id.tvPracticeContent)
        val btnStartPractice = findViewById<TextView>(R.id.btnStartPractice)

        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        val trainingId = intent.getStringExtra("TRAINING_ID") ?: ""
        val trainingTitle = intent.getStringExtra("TRAINING_TITLE") ?: "훈련 제목"
        tvTitle.text = trainingTitle

        fun buildSpannable(vararg parts: Pair<String, Int>): SpannableStringBuilder {
            val builder = SpannableStringBuilder()
            parts.forEach { (text, sizeSp) ->
                val start = builder.length
                builder.append(text.trimIndent() + "\n\n")
                builder.setSpan(AbsoluteSizeSpan(sizeSp, true), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (sizeSp >= 18) {
                    builder.setSpan(StyleSpan(Typeface.BOLD), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            return builder
        }

        val content = when (trainingId) {
            "bt_detail_002" -> buildSpannable(
                "DAY 1 - 내 몸에 귀기울이기" to 19,
                "목표: 판단 없이 몸 전체 감각을 고르게 인식하기" to 16,
                "이렇게 진행돼요." to 18,
                "- 총 6분간 진행됩니다.\n- 머리부터 발끝까지 천천히 살펴봅니다.\n- 지금 이 순간, 그저 느껴보기만 해보세요." to 16,
                "[훈련 전 도움말]" to 18,
                "- 오늘은 눈을 감고 머리부터 발끝까지 몸 전체를 살펴볼 거예요. 마치 스캔하듯이요.\n- 몸의 무게감, 따뜻함, 긴장된 부위, 닿는 느낌을 판단하지 않고 그대로 느껴보는 연습입니다.\n- 잘하려고 애쓰지 않아도 괜찮아요. 느껴지는 그대로,\n  그 자리에 머무는 것이 목적이에요." to 16
            )

            "bt_detail_003" -> buildSpannable(
                "DAY 2 - 먹기명상(음식의 오감 알아차리기)" to 19,
                "목표: 한 입의 음식을 천천히 먹으며, 오감과 감정을 인식하기" to 16,
                "[이렇게 진행돼요]" to 18,
                "- 배가 너무 고프지 않은 상태에서 참여해 주세요.\n- 너무 많은 양 말고, 한 입만 준비해주세요.\n- 어색하거나 집중이 안 되어도 괜찮아요." to 16,
                "* 먹기 명상에 좋은 음식\n- 건포도, 견과류, 말린 과일, 초콜릿 한 조각, 귤, 딸기 등\n  작은 과일과 같은 음식" to 16,
                "* 먹기 명상에 피해야할 음식\n  국물음식, 너무 맵거나 짠 음식, 비빔밥, 잡채 같은 복합음식" to 16
            )

            "bt_detail_004" -> buildSpannable(
                "DAY 3 - 감정과 몸 연결하기" to 19,
                "목표: 감정을 떠올리고, 그때 몸의 반응을 부드럽게 느껴보기" to 16,
                "“ 감정은 생각보다 먼저 몸에 나타납니다.”" to 16,
                "[훈련 전 도움말]" to 18,
                "- 오늘은 최근의 감정을 떠올리고, 그 감정이 몸 어디에\n  어떻게 나타나는지 알아차려볼거에요.\n- 감정을 억누르거나 바꾸려 하지 않고, 그 느낌이 몸에서 어떤 감각으로 있는지 조용히 느껴봅니다.\n- 어렵지 않아요. ‘그냥 불편한 곳이 어디지?’, ‘어떤 느낌이지?‘ 하고 관찰하는 연습입니다." to 16
            )

            "bt_detail_005" -> buildSpannable(
                "DAY 4 - 특정 감각에 집중하기" to 19,
                "목표: 특정 감각에 관심을 집중하며 주의산만 회복을 연습하기" to 16,
                "[감각 집중 훈련을 위한 도움말]" to 18,
                "1. 호흡의 움직임\n- 숨을 들이쉴 때, 배나 가슴이 살짝 올라오는 느낌을 느껴보세요.\n- 코끝을 지나는 공기의 흐름이느껴질 수도 있어요." to 16,
                "2. 심장박동\n- 가슴 안쪽에서 쿵쿵 뛰는 느낌이 있을 수 있어요.\n- 손이나 귀에 집중하면 미세한 박동이 더 느껴질 수 있어요." to 16,
                "3. 발바닥의 무게감\n- 바닥에 닿아 있는 발바닥이 어떻게 눌리고 있는지 느껴보세요.\n- 무게감이나 따뜻함, 약한 압박이 있을 수 있어요." to 16
            )

            "bt_detail_006" -> buildSpannable(
                "DAY 5 - 기본적인 바디스캔(감각 알아차리기)" to 19,
                "목표: 몸의 각 부위에 주의를 이동시키며, “ 지금 이 부위에서 어떤 감각이 느껴지는지 알아차리기" to 16,
                "[이렇게 진행돼요]" to 18,
                "- 머리부터 발끝까지 감각을 따라갑니다.\n- 감각이 없어도 괜찮고, 판단하지 않고 관찰하는 것이 중요해요.\n- “잘하려고 하지 않아도 괜찮아요”" to 16
            )

            "bt_detail_007" -> buildSpannable(
                "DAY 6 - 바디스캔(감각의 변화 알아차리기)" to 19,
                "목표: 바디스캔 훈련을 하며, 감각이 처음보다 어떻게 달라졌는지 느끼기" to 16,
                "[이렇게 진행돼요]" to 18,
                "- 어제와 비슷한 흐름으로 진행되지만, 조금 더 익숙하게\n  느껴질 수 있어요.\n- 잘하려고 하지 않아도 괜찮아요\n- 그저 '지금 여기'의 몸을 있는 그대로 관찰합니다." to 16,
                "[마음가짐]" to 18,
                "- 감각이 느껴지지 않아도 괜찮아요\n- 중요한 건' 느끼려는 태도' 그 자체 입니다.\n- 생각이 떠오르면 생각이 '떠올랐구나' 하고 다시 돌아오면 됩니다." to 16
            )

            "bt_detail_008" -> buildSpannable(
                "DAY 7 - 먹기명상(먹기명상을 통한 감정과 신체 연결 알아차림)" to 19,
                "목표: 감정이 먹는 행동에 어떤 영향을 주는지 알아차리고,\n       반응 관찰하기" to 16,
                "[이렇게 진행돼요]" to 18,
                "①. 오늘 먹고 싶은 음식 하나 떠올려보세요.\n②. ‘왜' 먹고 싶은지 자문해봅니다.\n③. 한 입 먹기 전, 감정을 살펴보고,\n④. 한 입 먹은 후, 감정의 변화를 알아차려봅니다.\n⑤. 떠오르는 감정에 이름 붙이지 않고 흘려보냅니다." to 16,
                "[유의점]" to 18,
                "- 감정이 떠오를 수 있어요.\n → 괜찮습니다. 판단하지 않고, 그냥 느껴보세요.\n- '왜' 먹고 싶은가를 억지로 분석하지 않아도 됩니다." to 16
            )

            else -> SpannableStringBuilder("준비 중인 연습입니다.")
        }

        tvContent.text = content

        btnStartPractice.setOnClickListener {
            when (trainingId) {
                "bt_detail_002" -> showEncouragementDialog("DAY 1, 집중이 안되는 것은 누구나 그렇습니다. 오늘도 잘 해내실 거예요. 🌱", trainingId, trainingTitle)
                "bt_detail_003" -> showEncouragementDialog("DAY 2, 잘하려 애쓰지 않아도 괜찮아요. 이미 충분합니다.🍇", trainingId, trainingTitle)
                "bt_detail_004" -> showEncouragementDialog("DAY 3, 훈련을 한다고 해서 감정이 곧바로 사라지는 것이 아닌 감정을 다스릴 준비를 하는 것 입니다. \uD83D\uDE0C", trainingId, trainingTitle)
                else -> goToPractice(trainingId, trainingTitle)
            }
        }
    }

    private fun showEncouragementDialog(message: String, trainingId: String, trainingTitle: String) {
        AlertDialog.Builder(this)
            .setTitle("응원 메시지")
            .setMessage(message)
            .setPositiveButton("시작하기") { _, _ ->
                goToPractice(trainingId, trainingTitle)
            }
            .show()
    }

    private fun goToPractice(trainingId: String, trainingTitle: String) {
        val intent = Intent(this, BodyTrainingPracticeActivity::class.java).apply {
            putExtra("TRAINING_ID", trainingId)
            putExtra("TRAINING_TITLE", trainingTitle)
        }
        startActivity(intent)
    }
}
