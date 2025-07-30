package com.example.emotionalapp.ui.emotion

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SelectReportActivity : AppCompatActivity() {

    private lateinit var lineChartMind: LineChart
    private lateinit var lineChartBody: LineChart
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select_report)

        lineChartMind = findViewById(R.id.lineChartMind)
        lineChartBody = findViewById(R.id.lineChartBody)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        loadEmotionDataAndDrawChart()
    }

    private fun toMidnight(date: Date): Date {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun loadEmotionDataAndDrawChart() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("user").document(email).get().addOnSuccessListener { document ->
            val signupTimestamp = document.getTimestamp("signupDate") ?: return@addOnSuccessListener
            val signupDate = signupTimestamp.toDate()
            val signupDateMidnight = toMidnight(signupDate)

            db.collection("user").document(email).collection("emotionSelect")
                .get()
                .addOnSuccessListener { result ->
                    val dataMap = mutableMapOf<Pair<Int, String>, MutableMap<String, Any>>() // (dayIdx, timeType) -> data

                    for (doc in result) {
                        val dateTimestamp = doc.getTimestamp("date") ?: continue
                        val date = dateTimestamp.toDate()
                        val recordDateMidnight = toMidnight(date)

                        // 회원가입일 기준 0일차, 1일차, 2일차 ... 6일차 계산
                        val diffDays = ((recordDateMidnight.time - signupDateMidnight.time) / (1000L * 60 * 60 * 24)).toInt()
                        if (diffDays !in 0..6) continue

                        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                        cal.time = date
                        val hour = cal.get(Calendar.HOUR_OF_DAY)
                        val timeType = if (hour < 12) "11" else "19"

                        val key = Pair(diffDays, timeType)
                        val existing = dataMap[key]
                        val existingDate = existing?.get("timestamp") as? Date
                        if (existingDate == null || date.after(existingDate)) {
                            dataMap[key] = mutableMapOf(
                                "mind" to (doc.getString("mind") ?: "보통"),
                                "body" to (doc.getString("body") ?: "보통"),
                                "timestamp" to date
                            )
                        }
                    }

                    val mindStates = listOf("매우 안 좋음", "안 좋음", "보통", "좋음", "매우 좋음")
                    val bodyStates = listOf("매우 이완됨", "이완됨", "보통", "각성", "매우 각성됨")

                    val mind11 = MutableList<Entry?>(7) { null }
                    val mind19 = MutableList<Entry?>(7) { null }
                    val body11 = MutableList<Entry?>(7) { null }
                    val body19 = MutableList<Entry?>(7) { null }

                    for ((key, value) in dataMap) {
                        val (dayIdx, time) = key
                        val mind = value["mind"] as String
                        val body = value["body"] as String

                        val mindVal = mindStates.indexOf(mind).takeIf { it >= 0 }?.toFloat() ?: 2f
                        val bodyVal = bodyStates.indexOf(body).takeIf { it >= 0 }?.toFloat() ?: 2f

                        when (time) {
                            "11" -> {
                                mind11[dayIdx] = Entry(dayIdx.toFloat(), mindVal)
                                body11[dayIdx] = Entry(dayIdx.toFloat(), bodyVal)
                            }
                            "19" -> {
                                mind19[dayIdx] = Entry(dayIdx.toFloat(), mindVal)
                                body19[dayIdx] = Entry(dayIdx.toFloat(), bodyVal)
                            }
                        }
                    }

                    drawLineChart(lineChartMind, "마음 상태", mind11, mind19, mindStates)
                    drawLineChart(lineChartBody, "몸 상태", body11, body19, bodyStates)
                }
        }
    }



    private fun drawLineChart(
        chart: LineChart,
        label: String,
        entries11: List<Entry?>,
        entries19: List<Entry?>,
        yLabels: List<String>
    ) {
        val dataSet11 = LineDataSet(entries11.filterNotNull(), "11시").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            setDrawValues(false)
            lineWidth = 2f
        }
        val dataSet19 = LineDataSet(entries19.filterNotNull(), "19시").apply {
            color = Color.RED
            setCircleColor(Color.RED)
            setDrawValues(false)
            lineWidth = 2f
        }

        val lineData = LineData(dataSet11, dataSet19)
        chart.data = lineData
        chart.description.isEnabled = false

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            axisMinimum = 0f
            axisMaximum = 6f // 항상 7일 기준 고정
            valueFormatter = object : ValueFormatter() {
                private val days = listOf("1일차", "2일차", "3일차", "4일차", "5일차", "6일차", "7일차")
                override fun getAxisLabel(value: Float, axis: AxisBase?) =
                    days.getOrNull(value.toInt()) ?: value.toString()
            }
        }

        chart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)  // 차트 내부가 아니라 외부에 표시
            yOffset = 16f         // x축과의 간격 (기본 0~6f)
        }


        chart.axisLeft.apply {
            granularity = 1f
            axisMinimum = 0f
            axisMaximum = 4f
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    val idx = value.toInt()
                    return yLabels.getOrNull(idx) ?: ""
                }
            }
        }

        chart.axisRight.isEnabled = false
        chart.invalidate()
    }
}
