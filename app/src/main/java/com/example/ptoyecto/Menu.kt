package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Menu : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val welcomeTextView: TextView = findViewById(R.id.textView3)

        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("usuarios").document(uid)

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombreUsuario = document.getString("nombre")
                    welcomeTextView.text = "Bienvenido, $nombreUsuario"
                } else {
                    welcomeTextView.text = "Bienvenido"
                }
            }.addOnFailureListener {
                welcomeTextView.text = "Bienvenido"
            }
        } else {
            welcomeTextView.text = "Bienvenido"
        }

        setupMenuButtons()
    }

    private fun setupMenuButtons() {
        findViewById<CardView>(R.id.eventos_button).setOnClickListener {
            startActivity(Intent(this, Eventos::class.java))
        }

        findViewById<CardView>(R.id.servicios_button).setOnClickListener {
            startActivity(Intent(this, Soporte::class.java))
        }

        findViewById<CardView>(R.id.mapa_button).setOnClickListener {
            startActivity(Intent(this, Mapa::class.java))
        }

        findViewById<CardView>(R.id.horarios_button).setOnClickListener {
            startActivity(Intent(this, Horario::class.java))
        }

        findViewById<ImageView>(R.id.cuenta).setOnClickListener {
            startActivity(Intent(this, Perfil::class.java))
        }

        findViewById<CardView>(R.id.logout_button).setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ -> logout() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, Iniciar_sesion::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
