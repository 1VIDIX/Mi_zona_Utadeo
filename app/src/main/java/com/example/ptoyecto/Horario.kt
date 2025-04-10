package com.example.ptoyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.api.Distribution.BucketOptions.Linear

class Horario : AppCompatActivity(), FormularioHorarioDialog.OnFormularioHorarioListener {
    private val ordenDias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    private lateinit var horarioLayout: LinearLayout
    private val horarioCompleto = mutableMapOf<String, TextView>() // Esto debe estar FUERA de la funcion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario)

        val welcomeTextView: TextView = findViewById(R.id.textView3)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            welcomeTextView.text = "Horario"
        }

        val back: Button = findViewById(R.id.buttonMenu)
        back.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        val botonAgregar: Button = findViewById(R.id.boton_agregar)
        botonAgregar.setOnClickListener {
            val dialog = FormularioHorarioDialog()
            dialog.listener = this // Asegúrate de configurar el listener
            dialog.show(supportFragmentManager, "FormularioHorario")
        }

        // Ajustes para la ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onFormularioHorarioConfirmado(curso: String, aula: String, dia: String, inicio: String, fin: String) {
        val texto = "Curso: $curso\nAula: $aula\nInicio: $inicio\nFin: $fin\n\n"

        if (horarioCompleto.containsKey(dia)) {
            val editable = horarioCompleto[dia]
            editable?.apply {
                text = text.toString() + texto
            }
        } else {
            val nuevoTextView = TextView(this)
            val borrar = TextView(this)
            nuevoTextView.text = "$dia\n$texto"
            nuevoTextView.setTextColor(getColor(R.color.black))
            nuevoTextView.textSize = 16f

            nuevoTextView.setPadding(16, 16, 16, 16)
            nuevoTextView.gravity = android.view.Gravity.CENTER_VERTICAL

            horarioCompleto[dia] = nuevoTextView
        }

        // Reordenar el layout
        val horarioLayout = findViewById<LinearLayout>(R.id.linearLayoutHorarios)
        horarioLayout.removeAllViews()

        for (diaOrdenado in ordenDias) {
            horarioCompleto[diaOrdenado]?.let {
                horarioLayout.addView(it)
            }
        }
    }





}
