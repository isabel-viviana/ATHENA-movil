package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentMockConfig : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_mock_config, container, false)

        val rbCompleto = view.findViewById<RadioButton>(R.id.rbCompleto)
        val rbMini = view.findViewById<RadioButton>(R.id.rbMini)
        val tvDuration = view.findViewById<TextView>(R.id.tvDuration)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnStartExam = view.findViewById<Button>(R.id.btnStartExam)

        val examTitle = arguments?.getString("EXAM_TITLE") ?: "Simulacro Oficial Saber 11"

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
            if (activity is MainActivity) {
                (activity as MainActivity).selectFragment(FragmentSimulacros())
            }
        }

        btnStartExam?.setOnClickListener {
            val intent = Intent(requireContext(), ExamActivity::class.java).apply {
                putExtra("EXAM_TITLE", examTitle)
            }
            startActivity(intent)
        }

        return view
    }
}
