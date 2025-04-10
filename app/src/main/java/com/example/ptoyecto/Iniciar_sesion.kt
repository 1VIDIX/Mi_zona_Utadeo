package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.widget.ProgressBar
import android.view.View
import android.widget.TextView

class Iniciar_sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)

        val auth = FirebaseAuth.getInstance()
        val emailField: EditText = findViewById(R.id.Email)
        val passwordField: EditText = findViewById(R.id.contra_user)
        val loginButton: Button = findViewById(R.id.boton_init)
        val back1: Button = findViewById(R.id.back1)
        val showPassword: ImageView = findViewById(R.id.show_password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        back1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        var isPasswordVisible = false
        showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPassword.setImageResource(R.drawable.ic_eye_open)
            } else {
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
                showPassword.setImageResource(R.drawable.ic_eye_closed)
            }
            passwordField.setSelection(passwordField.text.length)
        }
        val recuperar: TextView = findViewById(R.id.textView4)
        recuperar.setOnClickListener {
            val intent = Intent(this, Recuperar_contra::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Menu::class.java)
                        startActivity(intent)
                        progressBar.visibility = View.GONE

                        finish()
                    } else {
                        Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}