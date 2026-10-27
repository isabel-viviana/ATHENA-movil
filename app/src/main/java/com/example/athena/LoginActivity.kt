package com.example.athena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogle = findViewById<TextView>(R.id.btnGoogle)
        val btnFacebook = findViewById<TextView>(R.id.btnFacebook)
        val btnApple = findViewById<TextView>(R.id.btnApple)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Allow any valid non-empty input for smooth testing and SENA presentation
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val socialPendingMessage = getString(R.string.social_pending_msg)
        btnGoogle.setOnClickListener {
            Toast.makeText(this, socialPendingMessage, Toast.LENGTH_SHORT).show()
        }
        btnFacebook.setOnClickListener {
            Toast.makeText(this, socialPendingMessage, Toast.LENGTH_SHORT).show()
        }
        btnApple.setOnClickListener {
            Toast.makeText(this, socialPendingMessage, Toast.LENGTH_SHORT).show()
        }

        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
