package com.example.athena

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Cargar pantalla de Inicio por defecto
        if (savedInstanceState == null) {
            loadFragment(FragmentInicio())
        }

        // Navegación fluida entre las 6 secciones principales
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_inicio -> FragmentInicio()
                R.id.nav_simulacros -> FragmentSimulacros()
                R.id.nav_practicas -> FragmentPracticas()
                R.id.nav_tutor -> FragmentTutor()
                R.id.nav_ranking -> FragmentRanking()
                R.id.nav_perfil -> FragmentPerfil()
                else -> FragmentInicio()
            }
            loadFragment(fragment)
            true
        }
    }

    // Método público para navegar a un fragment desde otra pantalla
    fun selectFragment(fragment: Fragment) {
        val navId = when (fragment) {
            is FragmentInicio -> R.id.nav_inicio
            is FragmentSimulacros -> R.id.nav_simulacros
            is FragmentPracticas -> R.id.nav_practicas
            is FragmentTutor -> R.id.nav_tutor
            is FragmentRanking -> R.id.nav_ranking
            is FragmentPerfil -> R.id.nav_perfil
            else -> R.id.nav_inicio
        }
        bottomNavigationView.selectedItemId = navId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
