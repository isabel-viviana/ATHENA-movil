package com.example.athena

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)

val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

    if (savedInstanceState == null) {
    loadFragment(DummyFragment("Inicio"))
    }

    bottomNavigationView.setOnItemSelectedListener { item ->
    val fragment: Fragment = when (item.itemId) {
    R.id.nav_inicio -> DummyFragment("Inicio")
    R.id.nav_simulacros -> DummyFragment("Simulacros")
    R.id.nav_practicas -> PracticasAreaFragment()
    R.id.nav_tutor -> DummyFragment("Tutor IA")
    R.id.nav_ranking -> DummyFragment("Ranking")
    R.id.nav_perfil -> DummyFragment("Perfil")
    else -> DummyFragment("Inicio")
    }
    loadFragment(fragment)
    true
    }
    }

    private fun loadFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, fragment)
    .commit()
    }
    }

    // Fragment temporal para pantallas pendientes de implementar
    class DummyFragment(private val title: String) : Fragment() {
    override fun onCreateView(
    inflater: android.view.LayoutInflater,
    container: android.view.ViewGroup?,
    savedInstanceState: Bundle?
    ): android.view.View? {
    val view = android.widget.TextView(requireContext())
    view.text = title
    view.textSize = 24f
    view.setTextColor(android.graphics.Color.WHITE)
    view.gravity = android.view.Gravity.CENTER
    return view
    }
    }

    // Fragment para tu vista de Prácticas por Área
    class PracticasAreaFragment : Fragment() {
    override fun onCreateView(
    inflater: android.view.LayoutInflater,
    container: android.view.ViewGroup?,
    savedInstanceState: Bundle?
    ): android.view.View? {
    return inflater.inflate(R.layout.fragment_practicas_area, container, false)
    }
    }
