package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val welcomeTextView: TextView = findViewById(R.id.textView3)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val cuenta: ImageView = findViewById(R.id.cuenta)


        cuenta.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
            finish()
        }
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

        val eventosButton = findViewById<CardView>(R.id.eventos_button)
        eventosButton.setOnClickListener {
            val intent = Intent(this, Eventos::class.java)
            startActivity(intent)
        }

        val serviciosButton = findViewById<CardView>(R.id.servicios_button)
        serviciosButton.setOnClickListener {

            android.widget.Toast.makeText(this, "Servicios - Próximamente", android.widget.Toast.LENGTH_SHORT).show()
        }

        val mapaButton = findViewById<CardView>(R.id.mapa_button)
        mapaButton.setOnClickListener {
            android.widget.Toast.makeText(this, "Mapa - Próximamente", android.widget.Toast.LENGTH_SHORT).show()
        }

        val horarioButton = findViewById<CardView>(R.id.horarios_button)
        horarioButton.setOnClickListener {
            val intent = Intent(this, Horario::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}