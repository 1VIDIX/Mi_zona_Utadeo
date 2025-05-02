package com.example.ptoyecto

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EventoDetalle : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evento_detalle)

        val eventId = intent.getStringExtra("EVENT_ID") ?: ""
        val eventTitle = intent.getStringExtra("EVENT_TITLE") ?: ""

        setupUI(eventId, eventTitle)

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUI(eventId: String, eventTitle: String) {
        findViewById<TextView>(R.id.event_title).text = eventTitle

        when (eventId) {
            "featured_event_id" -> {
                setupFeaturedEvent()
            }
            "1" -> {
                setupAndroidWorkshop()
            }
            "2" -> {
                setupAIConference()
            }
            else -> {
                findViewById<TextView>(R.id.event_description).text =
                    "Información detallada sobre este evento próximamente."
                findViewById<TextView>(R.id.event_date).text = "Fecha: Por determinar"
                findViewById<TextView>(R.id.event_time).text = "Hora: Por determinar"
                findViewById<TextView>(R.id.event_location).text = "Ubicación: Por determinar"
            }
        }
    }

    private fun setupFeaturedEvent() {
        findViewById<ImageView>(R.id.event_banner).setImageResource(R.drawable.event_banner)

        findViewById<TextView>(R.id.event_date).text = "Fecha: 29 de Abril, 2025"
        findViewById<TextView>(R.id.event_time).text = "Hora: 10:00 - 13:00"
        findViewById<TextView>(R.id.event_location).text = "Ubicación: Auditorio Principal"
        findViewById<TextView>(R.id.event_organizer).text = "Organiza: Facultad de Ciencias"
        findViewById<TextView>(R.id.event_category_label).text = "Académico"

        findViewById<TextView>(R.id.event_description).text = """
            La Jornada de Innovación Científica reúne a investigadores, académicos y estudiantes para presentar los últimos avances en ciencia y tecnología dentro de nuestra universidad.
            
            Durante este evento, tendrás la oportunidad de conocer proyectos innovadores, participar en talleres prácticos y establecer contacto con expertos en diversas áreas científicas.
            
            Agenda:
            
            10:00 - 10:30: Inauguración y presentación
            10:30 - 11:30: Conferencia magistral: "Ciencia para el futuro"
            11:30 - 12:00: Pausa para café
            12:00 - 13:00: Presentación de proyectos estudiantiles
            
            La entrada es libre para todos los estudiantes y personal universitario. ¡Te esperamos!
        """.trimIndent()

        findViewById<TextView>(R.id.speakers_title).text = "Ponentes:"
        findViewById<TextView>(R.id.speakers_list).text = """
            • Dra. María Rodriguez - Directora de Investigación
            • Dr. Carlos Jiménez - Investigador Principal
            • Ing. Laura Martínez - Coordinadora de Innovación
        """.trimIndent()
    }

    private fun setupAndroidWorkshop() {
        findViewById<ImageView>(R.id.event_banner).setImageResource(R.drawable.android_workshop)

        findViewById<TextView>(R.id.event_date).text = "Fecha: 29 de Abril, 2025"
        findViewById<TextView>(R.id.event_time).text = "Hora: 14:00 - 16:00"
        findViewById<TextView>(R.id.event_location).text = "Ubicación: Facultad de Ingeniería, Aula 103"
        findViewById<TextView>(R.id.event_organizer).text = "Organiza: Club de Desarrollo Móvil"
        findViewById<TextView>(R.id.event_category_label).text = "Académico"

        findViewById<TextView>(R.id.event_description).text = """
            Aprende a desarrollar aplicaciones Android desde cero con este taller práctico. 
            
            En esta sesión, cubriremos:
            - Configuración del entorno de desarrollo
            - Fundamentos de Kotlin
            - Creación de interfaces de usuario
            - Implementación de funcionalidades básicas
            
            Requisitos:
            - Computadora portátil con Android Studio instalado
            - Conocimientos básicos de programación
            
            Plazas limitadas. Se recomienda inscripción previa en el sitio web del Club de Desarrollo Móvil.
        """.trimIndent()

        findViewById<TextView>(R.id.speakers_title).text = "Instructores:"
        findViewById<TextView>(R.id.speakers_list).text = """
            • Ing. Fernando López - Android Developer
            • Ana García - Estudiante de último año y desarrolladora freelance
        """.trimIndent()
    }

    private fun setupAIConference() {
        findViewById<ImageView>(R.id.event_banner).setImageResource(R.drawable.ic_conference)

        findViewById<TextView>(R.id.event_date).text = "Fecha: 29 de Abril, 2025"
        findViewById<TextView>(R.id.event_time).text = "Hora: 15:30 - 17:30"
        findViewById<TextView>(R.id.event_location).text = "Ubicación: Auditorio Principal"
        findViewById<TextView>(R.id.event_organizer).text = "Organiza: Departamento de Informática"
        findViewById<TextView>(R.id.event_category_label).text = "Conferencia"

        findViewById<TextView>(R.id.event_description).text = """
            Conferencia sobre los aspectos éticos y sociales de la Inteligencia Artificial en el mundo actual.
            
            Esta charla abordará temas cruciales como:
            - Sesgos algorítmicos y su impacto social
            - Privacidad y uso de datos personales
            - Transparencia en los sistemas de IA
            - Regulaciones actuales y futuras
            
            Se otorgará certificado de asistencia a los participantes que se registren al inicio del evento.
        """.trimIndent()

        findViewById<TextView>(R.id.speakers_title).text = "Ponentes:"
        findViewById<TextView>(R.id.speakers_list).text = """
            • Dra. Elena Ruiz - Especialista en Ética de la IA
            • Prof. Miguel Hernández - Director del Laboratorio de IA
        """.trimIndent()
    }
}