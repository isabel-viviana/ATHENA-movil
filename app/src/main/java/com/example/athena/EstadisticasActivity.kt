package com.example.athena


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EstadisticasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_estadisticas)

        // Agregar la gráfica de evolución del puntaje
        val chartContainer =
            findViewById<FrameLayout>(R.id.chartContainer)

        chartContainer.addView(
            LineChartView(this),
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // Botón de análisis profundo
        findViewById<TextView>(R.id.btnAnalisis)
            .setOnClickListener {
                Toast.makeText(
                    this,
                    "Análisis profundo de ATHENA",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Botón para practicar inglés
        findViewById<TextView>(R.id.btnPracticarIngles)
            .setOnClickListener {
                Toast.makeText(
                    this,
                    "Abriendo práctica de Inglés",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}