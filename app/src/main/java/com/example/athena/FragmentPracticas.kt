package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class FragmentPracticas : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_practicas, container, false)

        setupPracticeButton(view, R.id.btnMatematicas, "Matemáticas")
        setupPracticeButton(view, R.id.btnCiencias, "Ciencias Naturales")
        setupPracticeButton(view, R.id.btnLectura, "Lectura Crítica")
        setupPracticeButton(view, R.id.btnSociales, "Sociales y Ciudadanas")
        setupPracticeButton(view, R.id.btnIngles, "Inglés")

        return view
    }

    private fun setupPracticeButton(rootView: View, buttonId: Int, areaName: String) {
        rootView.findViewById<Button>(buttonId)?.setOnClickListener {
            val intent = Intent(requireContext(), PracticeConfigActivity::class.java).apply {
                putExtra("AREA_NAME", areaName)
            }
            startActivity(intent)
        }
    }
}
