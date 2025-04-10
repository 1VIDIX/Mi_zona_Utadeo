package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth



class Recuperar_contra : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)


        val emailEditText = findViewById<EditText>(R.id.Email)
        val sendCodeButton = findViewById<Button>(R.id.btnSendCode)


        sendCodeButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                val intent = Intent(this, Recuperada::class.java)
                startActivity(intent)
                enviarCodigoConfirmacion(email)


            } else {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarCodigoConfirmacion(email: String) {
        val auth = FirebaseAuth.getInstance()
        auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)


        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Correo de recuperación enviado a $email",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}