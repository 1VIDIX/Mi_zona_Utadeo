package com.example.ptoyecto


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.OnScaleChangedListener


class Mapa : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)


        findViewById<Button>(R.id.buttonMenu).setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }

        val mapaCampus: PhotoView = findViewById(R.id.mapaCampus)

        mapaCampus.post {
            mapaCampus.setScale(1.0f, false)
            mapaCampus.setMinimumScale(1.0f)
            mapaCampus.setMaximumScale(5.0f)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}


