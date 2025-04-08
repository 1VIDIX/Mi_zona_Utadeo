package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Recuperar_contra : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var sendCodeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)


        emailEditText = findViewById(R.id.Email)
        sendCodeButton = findViewById(R.id.btnSendCode)

        sendCodeButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Se ha enviado un código de confirmación a: $email", Toast.LENGTH_LONG).show()

            // Intent para iniciar la actividad Restablecida
            val intent = Intent(this, Restablecida::class.java)
            startActivity(intent)
        }
    }
}