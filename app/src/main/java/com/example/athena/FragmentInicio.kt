package com.example.athena

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentInicio : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Manejo de acciones rápidas para navegar a Simulacros o Tutor IA
        view.findViewById<View>(R.id.btn_ir_simulacro)?.setOnClickListener {
            (activity as? MainActivity)?.selectFragment(FragmentSimulacros())
        }

        view.findViewById<View>(R.id.btn_ir_tutor)?.setOnClickListener {
            (activity as? MainActivity)?.selectFragment(FragmentTutor())
        }
    }
}
