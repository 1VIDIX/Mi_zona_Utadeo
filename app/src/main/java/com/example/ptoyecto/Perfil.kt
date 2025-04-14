package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Perfil : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameTextView: TextView
    private lateinit var idTextView: TextView
    private lateinit var deptTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var ethnicityTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var universityEmailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var editButton: CardView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameTextView = findViewById(R.id.perfil_nombre)
        idTextView = findViewById(R.id.profile_id)
        deptTextView = findViewById(R.id.profile_dept)
        cityTextView = findViewById(R.id.profile_city)
        genderTextView = findViewById(R.id.profile_gender)
        ethnicityTextView = findViewById(R.id.profile_ethnicity)
        emailTextView = findViewById(R.id.profile_email)
        universityEmailTextView = findViewById(R.id.profile_university_email)
        phoneTextView = findViewById(R.id.profile_phone)
        editButton = findViewById(R.id.boton_modificar)
        backButton = findViewById(R.id.back_button)

        if (auth.currentUser == null) {
            val intent = Intent(this, Iniciar_sesion::class.java)
            startActivity(intent)
            finish()
            return
        }
        loadUserData()

        editButton.setOnClickListener {
            val intent = Intent(this, Perfilmod::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil_principal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            loadUserData()
        } else {
            val intent = Intent(this, Iniciar_sesion::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun loadUserData() {
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Set user data to UI elements
                        nameTextView.text = document.getString("nombre") ?: "No informado"
                        idTextView.text = document.getString("cedula") ?: "No informado"
                        deptTextView.text = document.getString("departamento") ?: "No informada"
                        cityTextView.text = document.getString("municipio") ?: "No informado"
                        genderTextView.text = document.getString("sexo") ?: "No informado"
                        ethnicityTextView.text = document.getString("etnia") ?: "No informada"
                        emailTextView.text = document.getString("email") ?: "No informado"
                        phoneTextView.text = document.getString("telefono") ?: "No informado"
                        universityEmailTextView.text = document.getString("emailUniversidad") ?: "No informado"
                    } else {
                        setDefaultValues()
                    }
                }
                .addOnFailureListener {
                    setDefaultValues()
                }
        } else {
            setDefaultValues()
        }
    }

    private fun setDefaultValues() {
        nameTextView.text = "No informado"
        idTextView.text = "No informado"
        deptTextView.text = "No informada"
        cityTextView.text = "No informado"
        genderTextView.text = "No informado"
        ethnicityTextView.text = "No informada"
        emailTextView.text = "No informado"
        phoneTextView.text = "No informado"
        universityEmailTextView.text = "No informado"
    }
}