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

        // Navegación fluida entre las secciones principales
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_inicio -> FragmentInicio()
                R.id.nav_simulacros -> FragmentSimulacros()
                R.id.nav_practicas -> FragmentPracticas()
                R.id.nav_tutor -> FragmentTutor()
                R.id.nav_perfil -> FragmentPerfil()
                else -> FragmentInicio()
            }
            loadFragment(fragment)
            true
        }

        // Cargar pantalla e inicializar el fragment por defecto al abrir la actividad
        if (savedInstanceState == null) {
            val openTab = intent.getStringExtra("OPEN_TAB")
            val (defaultItemId, defaultFragment) = when (openTab) {
                "tutor" -> Pair(R.id.nav_tutor, FragmentTutor())
                "practicas" -> Pair(R.id.nav_practicas, FragmentPracticas())
                "estadisticas" -> Pair(R.id.nav_perfil, FragmentEstadisticas())
                else -> Pair(R.id.nav_inicio, FragmentInicio())
            }

            if (bottomNavigationView.selectedItemId == defaultItemId) {
                loadFragment(defaultFragment)
            } else {
                bottomNavigationView.selectedItemId = defaultItemId
            }
        }
    }

    // Método público para navegar a un fragment desde otra pantalla o botón interno
    fun selectFragment(fragment: Fragment) {
        val navId = when (fragment) {
            is FragmentInicio -> R.id.nav_inicio
            is FragmentSimulacros -> R.id.nav_simulacros
            is FragmentPracticas -> R.id.nav_practicas
            is FragmentTutor -> R.id.nav_tutor
            is FragmentPerfil -> R.id.nav_perfil
            else -> null
        }

        if (navId != null) {
            if (bottomNavigationView.selectedItemId == navId) {
                loadFragment(fragment)
            } else {
                bottomNavigationView.selectedItemId = navId
            }
        } else {
            // Carga directa para fragments de detalle como FragmentEstadisticas, FragmentRanking, etc.
            loadFragment(fragment)
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
