package com.example.athena

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val tvBackLogin = findViewById<TextView>(R.id.tvBackLogin)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Instrucciones enviadas al correo", Toast.LENGTH_SHORT).show()
            finish()
        }

        tvBackLogin.setOnClickListener {
            finish()
        }
    }
}
