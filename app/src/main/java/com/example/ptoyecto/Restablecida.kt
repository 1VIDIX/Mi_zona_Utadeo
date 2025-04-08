package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class Restablecida : AppCompatActivity() {

    private lateinit var regresarInicioButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecida)


        regresarInicioButton = findViewById(R.id.btnSendCode)


        regresarInicioButton.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java) // Replace MainActivity::class.java with your actual main activity class


            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish()
        }
    }
}