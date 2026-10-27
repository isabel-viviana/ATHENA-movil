package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EstadisticasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        val btnDeepAnalysis = findViewById<Button>(R.id.btnDeepAnalysis)
        val lineChart = findViewById<LineChartView>(R.id.lineChart)
        val btnPracticarIngles = findViewById<Button>(R.id.btnPracticarIngles)

        lineChart?.setData(listOf(275f, 290f, 310f, 305f, 325f, 340f))

        btnDeepAnalysis?.setOnClickListener {
            Toast.makeText(this, "Análisis Profundo (Próximamente)", Toast.LENGTH_SHORT).show()
        }

        btnPracticarIngles?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("OPEN_TAB", "practicas")
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
