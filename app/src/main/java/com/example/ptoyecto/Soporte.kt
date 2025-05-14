package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Soporte : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soporte)

        val contactanosTextView = findViewById<TextView>(R.id.contactanos)
        val sugerenciasTextView = findViewById<TextView>(R.id.comentarios)
        val recuperarContraTextView = findViewById<TextView>(R.id.resetPassword)
        val historialTextView = findViewById<TextView>(R.id.historial) // Encuentra el TextView


        contactanosTextView.setOnClickListener {
            val intent = Intent(this, SolucionProblemas::class.java)
            startActivity(intent)
        }

        sugerenciasTextView.setOnClickListener {
            val intent = Intent(this, SugerenciasActivity::class.java)
            startActivity(intent)
        }

        recuperarContraTextView.setOnClickListener {
            val intent = Intent(this, Recuperar_contra::class.java)
            startActivity(intent)
        }

        // Set click listener for historialTextView
        historialTextView.setOnClickListener {  // Correcto: adjunta el listener al TextView
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}

class SolucionProblemas : AppCompatActivity() {

    private lateinit var problemaEditText: EditText
    private lateinit var btnSend: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solucion_problemas)

        problemaEditText = findViewById(R.id.problema_edit_text)
        btnSend = findViewById(R.id.btn_enviar_problema)

        btnSend.setOnClickListener {
            val problemDescription = problemaEditText.text.toString().trim()

            if (problemDescription.isNotEmpty()) {
                sendErrorReport(problemDescription)
            } else {
                Toast.makeText(this, "Por favor, describe el problema antes de enviar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendErrorReport(description: String) {
        Toast.makeText(this@SolucionProblemas, "Reporte enviado: $description", Toast.LENGTH_LONG).show()
        problemaEditText.text.clear()
    }
}