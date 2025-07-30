package com.example.emotionalapp.ui.mind

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AutoReportActivity : AppCompatActivity() {

    private lateinit var answer1: TextView
    private lateinit var answer2: TextView
    private lateinit var answer3: TextView
    private lateinit var answer5: TextView
    private lateinit var optionContainerTrap2: LinearLayout
    private lateinit var btnBack: ImageView

    private val options = listOf(
        "성급하게 결론짓기\n -이 비행기가 추락할 확률은 90%야. (실제 확률은 0.000013%)",
        "최악을 생각하기\n -부모님이 집에 늦게 들어오시네. 사고를 당한 것 같아.",
        "긍정적인 면 무시하기\n -시험문제가 우연히 쉬워서 좋은 점수를 받았을 뿐이야.",
        "흑백사고\n -시험에서 100점을 받지 못한다면 나는 실패자야.",
        "점쟁이 사고 (지레짐작하기)\n -연주회를 망칠 거야, 공연을 하지 않겠어.",
        "독심술\n -한 번도 대화를 나누지는 않았지만, 쟤는 나를 좋아하지 않아.",
        "정서적 추리\n -애인이 일 때문에 늦는다고 했지만, 그게 아닌 것 같아. 직감이 와. 나를 속이는 게 틀림없어.",
        "꼬리표 붙이기\n -나는 멍청해.",
        "“해야만 한다“는 진술문\n -사람들은 모두 정직해야해. 거짓말을 하는 건 있을 수 없는 일이야.",
        "마술적 사고\n -내가 아버지에게 전화를 걸면 아버지는 사고를 피할 수 있을 거야."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_auto_report)

        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)
        answer5 = findViewById(R.id.answer5)
        optionContainerTrap2 = findViewById(R.id.optionContainerTrap2)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        val reportDateMillis = intent.getLongExtra("reportDateMillis", -1L)
        if (reportDateMillis != -1L) {
            loadReport(Date(reportDateMillis))
        } else {
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadReport(targetDate: Date) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("user")
            .document(user.email ?: "")
            .collection("mindAuto")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val timeStamp = document.getTimestamp("date")?.toDate()
                    if (timeStamp != null && timeStamp.time == targetDate.time) {
                        answer1.setText(document.getString("answer1") ?: "")
                        answer2.setText(document.getString("answer2") ?: "")
                        answer3.setText(document.getString("answer3") ?: "")
                        answer5.setText(document.getString("answer5") ?: "")

                        val trap = document.getString("trap")
                        showTrapCard(trap)
                        return@addOnSuccessListener
                    }
                }
                Toast.makeText(this, "기록을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "기록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showTrapCard(trap: String?) {
        optionContainerTrap2.removeAllViews()

        if (!trap.isNullOrEmpty()) {
            val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerTrap2, false) as CardView
            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = "-$trap"  // 앞에 "-" 붙이기로 했었음
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            optionContainerTrap2.addView(card)
        }
    }

}
