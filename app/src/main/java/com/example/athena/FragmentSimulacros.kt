package com.example.athena

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentSimulacros : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_simulacros, container, false)

        view.findViewById<View>(R.id.btn_iniciar_simulacro_1)?.setOnClickListener {
            navigateToConfig("Simulacro Tipo Admisión 2026-I")
        }
        view.findViewById<View>(R.id.btn_iniciar_simulacro_2)?.setOnClickListener {
            navigateToConfig("Simulacro Express: Razonamiento")
        }
        view.findViewById<View>(R.id.btn_iniciar_simulacro_3)?.setOnClickListener {
            navigateToConfig("Simulacro Especial: Bloque Ciencias")
        }

        return view
    }

    private fun navigateToConfig(title: String) {
        if (activity is MainActivity) {
            val fragment = FragmentMockConfig().apply {
                arguments = Bundle().apply {
                    putString("EXAM_TITLE", title)
                }
            }
            (activity as MainActivity).loadFragment(fragment)
        }
    }
}
