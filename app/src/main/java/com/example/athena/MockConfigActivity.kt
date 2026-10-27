package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MockConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock_config)

        val rbCompleto = findViewById<RadioButton>(R.id.rbCompleto)
        val rbMini = findViewById<RadioButton>(R.id.rbMini)
        val tvDuration = findViewById<TextView>(R.id.tvDuration)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnStartExam = findViewById<Button>(R.id.btnStartExam)

        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Simulacro Oficial Saber 11"

        rbCompleto?.setOnClickListener {
            rbCompleto.isChecked = true
            rbMini?.isChecked = false
            tvDuration?.text = "4 horas 30 min"
        }

        rbMini?.setOnClickListener {
            rbMini.isChecked = true
            rbCompleto?.isChecked = false
            tvDuration?.text = "1 hora 30 min"
        }

        btnCancel?.setOnClickListener {
            finish()
        }

        btnStartExam?.setOnClickListener {
            val intent = Intent(this, ExamActivity::class.java).apply {
                putExtra("EXAM_TITLE", examTitle)
            }
            startActivity(intent)
            finish()
        }
    }
}
