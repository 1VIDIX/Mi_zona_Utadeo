package com.example.ptoyecto

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
