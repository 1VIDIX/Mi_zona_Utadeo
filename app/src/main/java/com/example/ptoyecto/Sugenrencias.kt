package com.example.ptoyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SugerenciasActivity : AppCompatActivity() {

    private lateinit var sugerenciaEditText: EditText
    private lateinit var btnEnviarSugerencia: Button
    private lateinit var backButton: Button

    // Inicializar FirebaseAuth y Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sugerencias)

        sugerenciaEditText = findViewById(R.id.sugerencia_edit_text)
        btnEnviarSugerencia = findViewById(R.id.EnviarSugerencia)
        backButton = findViewById(R.id.back_button)

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnEnviarSugerencia.setOnClickListener {
            val sugerencia = sugerenciaEditText.text.toString().trim()

            if (sugerencia.isNotEmpty()) {
                // Obtener el ID del usuario actual
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    // Crear un mapa con los datos que quieres guardar
                    val sugerenciaData = hashMapOf(
                        "sugerencia" to sugerencia,
                        "usuarioId" to userId,
                        "timestamp" to System.currentTimeMillis()
                    )

                    // Guardar la sugerencia en Firestore
                    firestore.collection("sugerencias")
                        .add(sugerenciaData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Sugerencia enviada y guardada.", Toast.LENGTH_SHORT).show()
                            sugerenciaEditText.text.clear()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar la sugerencia: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Por favor, escribe tu sugerencia antes de enviar.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}