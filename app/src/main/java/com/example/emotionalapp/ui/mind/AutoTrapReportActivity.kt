package com.example.emotionalapp.ui.mind

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.FirebaseFirestore

class AutoTrapReportActivity : AppCompatActivity() {

    private lateinit var chart: BarChart

    private val trapOptions = listOf(
        "성급하게 결론짓기",
        "최악을 생각하기",
        "긍정적인 면 무시하기",
        "흑백사고",
        "점쟁이 사고 (지레짐작하기)",
        "독심술",
        "정서적 추리",
        "꼬리표 붙이기",
        "“해야만 한다“는 진술문",
        "마술적 사고"
    )

    private val trapCountMap = mutableMapOf<String, Int>().apply {
        trapOptions.forEach { this[it] = 0 }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_auto_trap_report)

        chart = findViewById(R.id.barChart)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        loadTrapStatistics()
    }

    private fun loadTrapStatistics() {
        val db = FirebaseFirestore.getInstance()
        val userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email

        if (userEmail != null) {
            val trapTask = db.collection("user").document(userEmail).collection("mindTrap").get()
            val autoTask = db.collection("user").document(userEmail).collection("mindAuto").get()

            com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(trapTask, autoTask)
                .addOnSuccessListener { results ->
                    trapCountMap.keys.forEach { trapCountMap[it] = 0 }

                    val originalTrapOptions = listOf(
                        "성급하게 결론짓기",
                        "최악을 생각하기",
                        "긍정적인 면 무시하기",
                        "흑백사고",
                        "점쟁이 사고 (지레짐작하기)",
                        "독심술",
                        "정서적 추리",
                        "꼬리표 붙이기",
                        "“해야만 한다“는 진술문",
                        "마술적 사고"
                    )

                    for (result in results) {
                        if (result is com.google.firebase.firestore.QuerySnapshot) {
                            for (doc in result.documents) {
                                val trapValue = doc.getString("trap")
                                trapValue?.let { trapStr ->
                                    originalTrapOptions.forEachIndexed { index, option ->
                                        if (trapStr.contains(option)) {
                                            val shortKey = trapOptions[index]
                                            trapCountMap[shortKey] = (trapCountMap[shortKey] ?: 0) + 1
                                        }
                                    }
                                }
                            }
                        }
                    }

                    drawChart()
                }
                .addOnFailureListener { e -> e.printStackTrace() }
        }
    }

    private fun drawChart() {
        val entries = mutableListOf<BarEntry>()

        trapOptions.forEachIndexed { index, option ->
            val count = trapCountMap[option] ?: 0
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
        }

        val dataSet = BarDataSet(entries, "생각의 덫 통계").apply {
            // 그라데이션 효과를 위한 색상 배열
            colors = listOf(
                Color.parseColor("#FF6B6B"),
                Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"),
                Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7"),
                Color.parseColor("#DDA0DD"),
                Color.parseColor("#FFB347"),
                Color.parseColor("#87CEEB"),
                Color.parseColor("#98FB98"),
                Color.parseColor("#F0E68C")
            )

            valueTextSize = 12f
            valueTextColor = Color.BLACK

            // 값 표시 포맷터
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}회"
                }
            }
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.7f // 바 두께 조정
        }

        chart.apply {
            data = barData

            // 차트 기본 설정
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            animateY(1200) // 애니메이션 변경

            // 사용자 조작 방지
            setScaleEnabled(false)
            setPinchZoom(false)
            setDoubleTapToZoomEnabled(false)
            isDragEnabled = false
            isHighlightPerTapEnabled = false

            // 차트 여백 설정 (충분한 하단 여백)
            setExtraOffsets(20f, 30f, 20f, 50f)

            // Y축 (오른쪽) 비활성화
            axisRight.isEnabled = false

            // Y축 (왼쪽) 설정
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                textSize = 11f
                textColor = Color.GRAY
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
            }

            // X축 설정
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(trapOptions)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f

                // 모든 레이블 표시
                setLabelCount(trapOptions.size, false)
                isGranularityEnabled = true

                // 텍스트 설정
                textSize = 10f
                textColor = Color.DKGRAY
                labelRotationAngle = 0f // 회전 제거

                // 선 및 그리드 설정
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineColor = Color.GRAY
                axisLineWidth = 1f

                // 레이블 위치 조정
                yOffset = 10f

                // 축의 최소/최대값 설정으로 모든 레이블 보장
                axisMinimum = -0.5f
                axisMaximum = (trapOptions.size - 1).toFloat() + 0.5f
            }

            // 차트 새로고침
            invalidate()
        }
    }
}