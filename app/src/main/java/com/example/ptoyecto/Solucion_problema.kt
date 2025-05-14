package com.example.ptoyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SolucionProblemasActivity : AppCompatActivity() {

    private lateinit var problemaEditText: EditText
    private lateinit var btnEnviarProblema: Button
    private lateinit var backButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solucion_problemas)

        problemaEditText = findViewById(R.id.problema_edit_text)
        btnEnviarProblema = findViewById(R.id.btn_enviar_problema)
        backButton = findViewById(R.id.back_button)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnEnviarProblema.setOnClickListener {
            val problemaTexto = problemaEditText.text.toString().trim()

            if (problemaTexto.isNotEmpty()) {
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    val data = hashMapOf(
                        "mensaje" to problemaTexto,
                        "usuarioId" to userId,
                        "timestamp" to System.currentTimeMillis()
                    )

                    // Guardar en la colección "problemas"
                    firestore.collection("problemas")
                        .add(data)
                        .addOnSuccessListener {
                            // También guardamos en "sugerencias" para que aparezca en historial
                            firestore.collection("sugerencias")
                                .add(data)

                            Toast.makeText(this, "Problema enviado correctamente.", Toast.LENGTH_SHORT).show()
                            problemaEditText.text.clear()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al enviar: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}