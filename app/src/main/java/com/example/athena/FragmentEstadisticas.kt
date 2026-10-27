package com.example.athena

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class FragmentEstadisticas : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_estadisticas, container, false)

        val btnDeepAnalysis = view.findViewById<Button>(R.id.btnDeepAnalysis)
        val lineChart = view.findViewById<LineChartView>(R.id.lineChart)
        val btnPracticarIngles = view.findViewById<Button>(R.id.btnPracticarIngles)

        lineChart?.setData(listOf(275f, 290f, 310f, 305f, 325f, 340f))

        btnDeepAnalysis?.setOnClickListener {
            Toast.makeText(requireContext(), "Análisis Profundo (Próximamente)", Toast.LENGTH_SHORT).show()
        }

        btnPracticarIngles?.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity).loadFragment(FragmentPracticas())
            }
        }

        return view
    }
}
