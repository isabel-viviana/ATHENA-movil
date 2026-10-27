package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPassword = findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmNewPassword = findViewById<TextInputEditText>(R.id.etConfirmNewPassword)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val tvBackLogin = findViewById<TextView>(R.id.tvBackLogin)

        btnReset.setOnClickListener {
            val password = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmNewPassword.text.toString().trim()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, R.string.error_password_length, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, R.string.error_password_match, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        tvBackLogin.setOnClickListener {
            finish()
        }
    }
}
