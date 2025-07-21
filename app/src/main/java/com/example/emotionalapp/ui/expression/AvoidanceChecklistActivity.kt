package com.example.emotionalapp.ui.expression
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.databinding.ActivityAvoidanceChecklistBinding

class AvoidanceChecklistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAvoidanceChecklistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvoidanceChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnStartDiary.setOnClickListener {
            val intent = Intent(this, AvoidanceDiaryFormActivity::class.java)
            startActivity(intent)
        }
    }
}