package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Recuperar_contra : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)


        val emailEditText = findViewById<EditText>(R.id.Email)
        val sendCodeButton = findViewById<Button>(R.id.btnSendCode)


        sendCodeButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                enviarCodigoConfirmacion(email)
            } else {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarCodigoConfirmacion(email: String) {
        // Simulación del envío de código (debes conectarlo con Firebase u otro servicio)
        Toast.makeText(this, "Código enviado a $email", Toast.LENGTH_LONG).show()


    }
}