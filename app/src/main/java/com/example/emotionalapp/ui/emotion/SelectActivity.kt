package com.example.emotionalapp.ui.emotion

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class SelectActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View
    private lateinit var contentView1: View
    private lateinit var contentView2: View

    private lateinit var mindButtons: List<LinearLayout>
    private lateinit var bodyButtons: List<LinearLayout>
    private var selectedMind = -1
    private var selectedBody = -1

    private lateinit var btnSelect: TextView
    private lateinit var lineChartMind: LineChart
    private lateinit var lineChartBody: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        // 상단 탭 및 뒤로가기
        btnBack = findViewById(R.id.btnBack)
        tabPractice = findViewById(R.id.tabPractice)
        tabRecord = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord = findViewById(R.id.underlineRecord)
        contentView1 = findViewById(R.id.contentView1)
        contentView2 = findViewById(R.id.contentView2)

        btnBack.setOnClickListener { finish() }

        tabPractice.setOnClickListener {
            selectTab(true)
        }
        tabRecord.setOnClickListener {
            selectTab(false)
            drawCharts() // 탭 전환 시 그래프 표시
        }

        // 감정 선택 버튼
        mindButtons = listOf(
            findViewById(R.id.btnMind1),
            findViewById(R.id.btnMind2),
            findViewById(R.id.btnMind3),
            findViewById(R.id.btnMind4),
            findViewById(R.id.btnMind5)
        )
        bodyButtons = listOf(
            findViewById(R.id.btnBody1),
            findViewById(R.id.btnBody2),
            findViewById(R.id.btnBody3),
            findViewById(R.id.btnBody4),
            findViewById(R.id.btnBody5)
        )

        setupFeelingButtons()

        btnSelect = findViewById(R.id.btnSelect)
        btnSelect.setOnClickListener {
            if (selectedMind == -1 || selectedBody == -1) {
                Toast.makeText(this, "마음과 몸의 감정을 선택해주세요", Toast.LENGTH_SHORT).show()
            } else {
                saveEmotionData()
                Toast.makeText(this, "감정이 기록되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 그래프 뷰
        lineChartMind = findViewById(R.id.lineChartMind)
        lineChartBody = findViewById(R.id.lineChartBody)

        // 초기화
        selectTab(true)
    }

    private fun selectTab(isPractice: Boolean) {
        underlinePractice.visibility = if (isPractice) View.VISIBLE else View.GONE
        underlineRecord.visibility = if (!isPractice) View.VISIBLE else View.GONE

        contentView1.visibility = if (isPractice) View.VISIBLE else View.GONE
        contentView2.visibility = if (!isPractice) View.VISIBLE else View.GONE

        tabPractice.setTextColor(getColor(if (isPractice) R.color.black else R.color.gray))
        tabRecord.setTextColor(getColor(if (!isPractice) R.color.black else R.color.gray))
    }

    private fun setupFeelingButtons() {
        mindButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedMind = index
                updateButtonStates(mindButtons, index)
            }
        }
        bodyButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedBody = index
                updateButtonStates(bodyButtons, index)
            }
        }
    }

    private fun updateButtonStates(buttons: List<LinearLayout>, selectedIndex: Int) {
        buttons.forEachIndexed { index, btn ->
            btn.alpha = if (index == selectedIndex) 1.0f else 0.3f
        }
    }

    private fun saveEmotionData() {
        // 여기에 로컬 DB 또는 ViewModel 저장 로직을 넣을 수 있음
        // selectedMind, selectedBody 값은 각각 0~4
    }

    private fun drawCharts() {
        val days = listOf("월", "화", "수", "목", "금", "토", "일")

        // 샘플 데이터: (마음) 11시 / 19시
        val mind11 = listOf(2f, 3f, 1f, 4f, 2f, 3f, 4f)
        val mind19 = listOf(3f, 2f, 2f, 5f, 3f, 2f, 4f)

        val body11 = listOf(1f, 2f, 3f, 3f, 2f, 4f, 3f)
        val body19 = listOf(2f, 3f, 4f, 4f, 3f, 5f, 4f)

        drawLineChart(lineChartMind, days, mind11, mind19, "11시", "19시")
        drawLineChart(lineChartBody, days, body11, body19, "11시", "19시")
    }

    private fun drawLineChart(
        chart: LineChart,
        xLabels: List<String>,
        data1: List<Float>,
        data2: List<Float>,
        label1: String,
        label2: String
    ) {
        val entries1 = data1.mapIndexed { index, value -> Entry(index.toFloat(), value) }
        val entries2 = data2.mapIndexed { index, value -> Entry(index.toFloat(), value) }

        val dataSet1 = LineDataSet(entries1, label1).apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 3f
        }

        val dataSet2 = LineDataSet(entries2, label2).apply {
            color = Color.RED
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 3f
        }

        val lineData = LineData(dataSet1, dataSet2)
        chart.data = lineData

        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(xLabels)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
        }

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        chart.invalidate()
    }
}
