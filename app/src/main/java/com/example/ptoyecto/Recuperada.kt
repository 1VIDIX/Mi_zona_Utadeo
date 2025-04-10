package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Recuperada : AppCompatActivity() {

    private lateinit var bienvenidaTextView: TextView
    private lateinit var mensajeTextView: TextView
    private lateinit var regresarInicioButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperada)


        bienvenidaTextView = findViewById(R.id.Bienvenida2)
        mensajeTextView = findViewById(R.id.textView)
        regresarInicioButton = findViewById(R.id.btnSendCode)

        regresarInicioButton.setOnClickListener {

            val intent = Intent(this, com.example.ptoyecto.Iniciar_sesion::class.java)
            startActivity(intent)
            finish()
        }

    }
}