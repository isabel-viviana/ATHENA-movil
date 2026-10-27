package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PracticeConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_config)

        val areaName = intent.getStringExtra("AREA_NAME") ?: "Matemáticas"
        val tvAreaSubtitle = findViewById<TextView>(R.id.tvAreaSubtitle)
        tvAreaSubtitle.text = "Práctica focalizada en $areaName (25 preguntas)"

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnStartPractice = findViewById<Button>(R.id.btnStartPractice)

        btnCancel.setOnClickListener {
            finish()
        }

        btnStartPractice.setOnClickListener {
            val intent = Intent(this, ExamActivity::class.java).apply {
                putExtra("EXAM_TITLE", "Práctica de $areaName")
                putExtra("IS_PRACTICE", true)
            }
            startActivity(intent)
            finish()
        }
    }
}
