package com.example.athena

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EstadisticasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
        }

        val lineChart = findViewById<LineChartView>(R.id.lineChart)
        lineChart?.setData(listOf(30f, 50f, 40f, 65f, 80f, 75f, 95f))

        val tvStatsSummary = findViewById<TextView>(R.id.tvStatsSummary)
        tvStatsSummary?.text = "Progreso general: 85% completado\nSimulacros realizados: 12\nPreguntas acertadas: 240/280"
    }
}
