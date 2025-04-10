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
import com.google.firebase.firestore.FirebaseFirestore

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        val nombreUser: EditText = findViewById(R.id.Nombre)
        val emailUser: EditText = findViewById(R.id.Email)
        val contraUser: EditText = findViewById(R.id.contra_user)
        val contraUserCheck: EditText = findViewById(R.id.contra_user_check)
        val btnRegistrar: Button = findViewById(R.id.boton_init)
        val showPassword: ImageView = findViewById(R.id.show_password)
        val showPassword2: ImageView = findViewById(R.id.show_password2)

        var isPasswordVisible = false
        showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                contraUser.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPassword.setImageResource(R.drawable.ic_eye_open)
            } else {
                contraUser.transformationMethod = PasswordTransformationMethod.getInstance()
                showPassword.setImageResource(R.drawable.ic_eye_closed)
            }
            contraUser.setSelection(contraUser.text.length)
        }

        var isPasswordCheckVisible = false
        showPassword2.setOnClickListener {
            isPasswordCheckVisible = !isPasswordCheckVisible
            if (isPasswordCheckVisible) {
                contraUserCheck.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPassword2.setImageResource(R.drawable.ic_eye_open)
            } else {
                contraUserCheck.transformationMethod = PasswordTransformationMethod.getInstance()
                showPassword2.setImageResource(R.drawable.ic_eye_closed)
            }
            contraUserCheck.setSelection(contraUserCheck.text.length)
        }

        btnRegistrar.setOnClickListener {
            val nombre = nombreUser.text.toString().trim()
            val email = emailUser.text.toString().trim()
            val contraseña = contraUser.text.toString().trim()
            val confirmarContraseña = contraUserCheck.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraseña != confirmarContraseña) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid
                        val db = FirebaseFirestore.getInstance()

                        val userMap = hashMapOf(
                            "nombre" to nombre,
                            "email" to email
                        )

                        if (uid != null) {
                            db.collection("usuarios").document(uid).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al guardar datos: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
