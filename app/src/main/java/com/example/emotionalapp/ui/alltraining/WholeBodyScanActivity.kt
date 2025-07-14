package com.example.emotionalapp.ui.alltraining

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.BodyScanRecordAdapter

class WholeBodyScanActivity : AppCompatActivity() {

    private lateinit var tabPractice: TextView
    private lateinit var tabRecord: TextView
    private lateinit var underlinePractice: View
    private lateinit var underlineRecord: View
    private lateinit var layoutPractice: View
    private lateinit var layoutRecord: View
    private lateinit var recordRecycler: RecyclerView
    private lateinit var btnStartPractice: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_scan)

        // 뒤로가기 & 타이틀
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_page_title).text = "전체 몸 스캔 인식하기"

        // 뷰 바인딩
        tabPractice      = findViewById(R.id.tabPractice)
        tabRecord        = findViewById(R.id.tabRecord)
        underlinePractice = findViewById(R.id.underlinePractice)
        underlineRecord   = findViewById(R.id.underlineRecord)
        layoutPractice   = findViewById(R.id.layoutPractice)
        layoutRecord     = findViewById(R.id.layoutRecord)
        recordRecycler   = findViewById(R.id.rvRecords)
        btnStartPractice = findViewById(R.id.btnStartPractice)

        // 탭 클릭
        tabPractice.setOnClickListener { selectTab(practice = true) }
        tabRecord .setOnClickListener { selectTab(practice = false) }

        // RecyclerView
        recordRecycler.layoutManager = LinearLayoutManager(this)
        recordRecycler.adapter = BodyScanRecordAdapter(
            listOf("2025년 6월 29일", "2025년 6월 30일", "2025년 7월 2일")
        )

        // 버튼 클릭 (연습 시작)
        btnStartPractice.setOnClickListener {
            //실제 스캔 연습 로직
        }

        // 기본 탭은 연습하기
        selectTab(practice = true)
    }

    private fun selectTab(practice: Boolean) {
        // 탭 글자색
        tabPractice.setTextColor(
            resources.getColor(if (practice) R.color.black else R.color.gray, null)
        )
        tabRecord.setTextColor(
            resources.getColor(if (practice) R.color.gray else R.color.black, null)
        )

        // 언더라인
        underlinePractice.visibility = if (practice) View.VISIBLE else View.GONE
        underlineRecord  .visibility = if (practice) View.GONE    else View.VISIBLE

        // 컨텐츠 토글
        layoutPractice.visibility = if (practice) View.VISIBLE else View.GONE
        layoutRecord  .visibility  = if (practice) View.GONE    else View.VISIBLE
    }
}
