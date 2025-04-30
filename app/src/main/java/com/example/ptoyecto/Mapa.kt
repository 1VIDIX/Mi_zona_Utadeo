package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.chrisbanes.photoview.PhotoView

class Mapa : AppCompatActivity() {

    private lateinit var mapaCampus: PhotoView
    private lateinit var mapaOverlay: MapaOverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)


        mapaCampus = findViewById(R.id.mapaCampus)
        mapaOverlay = findViewById(R.id.mapaOverlay)

        findViewById<Button>(R.id.buttonMenu).setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }


        mapaCampus.post {
            mapaCampus.setScale(3.0f, false)
            mapaCampus.setMinimumScale(1.0f)
            mapaCampus.setMaximumScale(5.0f)
        }

        mapaCampus.setOnMatrixChangeListener { _ ->
            mapaOverlay.updateMatrix(mapaCampus.imageMatrix)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}