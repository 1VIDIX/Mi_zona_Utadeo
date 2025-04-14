package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Perfilmod : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var deptEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var ethnicityEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var universityEmailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_mod)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.edit_nombre)
        idEditText = findViewById(R.id.edit_id)
        deptEditText = findViewById(R.id.edit_dept)
        cityEditText = findViewById(R.id.edit_city)
        genderEditText = findViewById(R.id.edit_gender)
        ethnicityEditText = findViewById(R.id.edit_ethnicity)
        emailEditText = findViewById(R.id.edit_email)
        universityEmailEditText = findViewById(R.id.edit_university_email)
        phoneEditText = findViewById(R.id.edit_phone)
        saveButton = findViewById(R.id.boton_guardar)
        backButton = findViewById(R.id.back_button)

        loadUserData()

        saveButton.setOnClickListener {
            saveUserData()
        }

        backButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_profile_principal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid

            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        nameEditText.setText(document.getString("nombre") ?: "")
                        idEditText.setText(document.getString("cedula") ?: "")
                        deptEditText.setText(document.getString("departamento") ?: "")
                        cityEditText.setText(document.getString("municipio") ?: "")
                        genderEditText.setText(document.getString("sexo") ?: "")
                        ethnicityEditText.setText(document.getString("etnia") ?: "")
                        emailEditText.setText(document.getString("email") ?: "")
                        phoneEditText.setText(document.getString("telefono") ?: "")
                        universityEmailEditText.setText(document.getString("emailUniversidad") ?: "")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar los datos del perfil", Toast.LENGTH_SHORT).show()
                }
        } else {
            val intent = Intent(this, Iniciar_sesion::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun saveUserData() {
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid
            val userData = hashMapOf(
                "nombre" to nameEditText.text.toString(),
                "cedula" to idEditText.text.toString(),
                "departamento" to deptEditText.text.toString(),
                "municipio" to cityEditText.text.toString(),
                "sexo" to genderEditText.text.toString(),
                "etnia" to ethnicityEditText.text.toString(),
                "email" to emailEditText.text.toString(),
                "telefono" to phoneEditText.text.toString(),
                "emailUniversidad" to universityEmailEditText.text.toString()
            )
            db.collection("usuarios").document(uid).update(userData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                }
        }
    }
}