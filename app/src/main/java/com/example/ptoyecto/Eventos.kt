package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class
Eventos  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        val titleEventosTextView: TextView = findViewById(R.id.title_eventos)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser


        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        val cafeteriaCategory = findViewById<android.widget.LinearLayout>(R.id.cafeteria_category)
        cafeteriaCategory.setOnClickListener {
        }

        val deportesCategory = findViewById<android.widget.LinearLayout>(R.id.deportes_category)
        deportesCategory.setOnClickListener {
        }

        val bibliotecaCategory = findViewById<android.widget.LinearLayout>(R.id.biblioteca_category)
        bibliotecaCategory.setOnClickListener {
        }

        val spacesLink = findViewById<TextView>(R.id.spaces_link)
        spacesLink.setOnClickListener {
        }

        val bibliotecaCard = findViewById<androidx.cardview.widget.CardView>(R.id.biblioteca_card)
        bibliotecaCard.setOnClickListener {
        }

        val calmaCard = findViewById<androidx.cardview.widget.CardView>(R.id.calma_card)
        calmaCard.setOnClickListener {
        }
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("usuarios").document(uid)

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
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