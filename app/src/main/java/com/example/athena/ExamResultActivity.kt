package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ExamResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_result)

        val isPractice = intent.getBooleanExtra("IS_PRACTICE", false)
        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: ""

        val tvHeaderTitle = findViewById<TextView>(R.id.tvHeaderTitle)
        val tvHeaderSubtitle = findViewById<TextView>(R.id.tvHeaderSubtitle)

        if (isPractice) {
            tvHeaderTitle?.text = "¡Práctica completada!"
            tvHeaderSubtitle?.text = "Resultados de $examTitle"
        }

        val btnReviewAnswers = findViewById<Button>(R.id.btnReviewAnswers)
        val btnAiTutor = findViewById<Button>(R.id.btnAiTutor)
        val btnDashboard = findViewById<Button>(R.id.btnDashboard)

        btnReviewAnswers.setOnClickListener {
            Toast.makeText(this, "Revisión de respuestas (Visual)", Toast.LENGTH_SHORT).show()
        }

        btnAiTutor.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("OPEN_TAB", "tutor")
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        btnDashboard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
