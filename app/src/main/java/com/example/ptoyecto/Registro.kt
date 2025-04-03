package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth

        val nombreUser: EditText = findViewById(R.id.Nombre)
        val emailUser: EditText = findViewById(R.id.Email)
        val contraseñaUser: EditText = findViewById(R.id.contraseña_user)
        val contraseñaUserCheck: EditText = findViewById(R.id.contraseña_user_check)
        val btnRegistrar: Button = findViewById(R.id.boton_init)

        btnRegistrar.setOnClickListener {
            val nombre = nombreUser.text.toString().trim()
            val email = emailUser.text.toString().trim()
            val contraseña = contraseñaUser.text.toString().trim()
            val confirmarContraseña = contraseñaUserCheck.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraseña != confirmarContraseña) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
