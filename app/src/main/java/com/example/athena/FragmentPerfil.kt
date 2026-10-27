package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentPerfil : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)
        view.findViewById<View>(R.id.layoutEstadisticas)?.setOnClickListener {
            val intent = Intent(requireContext(), EstadisticasActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}
