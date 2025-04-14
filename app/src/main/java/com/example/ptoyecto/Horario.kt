package com.example.ptoyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences

class Horario : AppCompatActivity(), FormularioHorarioDialog.OnFormularioHorarioListener {

    private val ordenDias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    private lateinit var horarioLayout: LinearLayout
    private val horarioCompleto = mutableMapOf<String, TextView>()
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario)

        horarioLayout = findViewById(R.id.linearLayoutHorarios)
        sharedPrefs = getSharedPreferences("HorariosPrefs", Context.MODE_PRIVATE)

        val welcomeTextView: TextView = findViewById(R.id.textView3)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) welcomeTextView.text = "Horario"

        findViewById<Button>(R.id.buttonMenu).setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }

        findViewById<Button>(R.id.boton_agregar).setOnClickListener {
            val dialog = FormularioHorarioDialog()
            dialog.listener = this
            dialog.show(supportFragmentManager, "FormularioHorario")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cargarHorariosDesdePrefs()
    }

    override fun onFormularioHorarioConfirmado(
        curso: String,
        aula: String,
        dia: String,
        inicio: String,
        fin: String
    ) {
        val texto = "Curso: $curso\nAula: $aula\nInicio: $inicio\nFin: $fin\n\n"

        val textoExistente = horarioCompleto[dia]?.text?.toString()
        val textoActualizado = textoExistente + texto

        val nuevoTextView = crearTextViewDia(dia, textoActualizado)
        horarioCompleto[dia] = nuevoTextView

        guardarHorariosEnPrefs()
        actualizarLayout()
    }


    private fun crearTextViewDia(dia: String, contenido: String): TextView {
        val textView = TextView(this)
        textView.text = "$dia\n$contenido"
        textView.setTextColor(getColor(R.color.black))
        textView.textSize = 16f
        textView.setPadding(16, 16, 16, 16)
        textView.setBackgroundResource(R.drawable.rounded_background)

        textView.setOnClickListener {
            // Extraer valores del texto actual para editar
            val lineas = contenido.trim().split("\n")
            val curso = lineas[0].removePrefix("Curso: ")
            val aula = lineas[1].removePrefix("Aula: ")
            val inicio = lineas[2].removePrefix("Inicio: ")
            val fin = lineas[3].removePrefix("Fin: ")

            // Mostrar formulario con valores actuales
            val dialog = FormularioHorarioDialog()
            dialog.listener = this
            dialog.setValoresIniciales(curso, aula, dia, inicio, fin)
            dialog.show(supportFragmentManager, "FormularioHorario")

            // Eliminar el contenido viejo para reemplazarlo
            horarioCompleto.remove(dia)
            guardarHorariosEnPrefs()
            actualizarLayout()

        }

        return textView
    }

    private fun actualizarLayout() {
        horarioLayout.removeAllViews()
        for (dia in ordenDias) {
            horarioCompleto[dia]?.let {
                horarioLayout.addView(it)
            }
        }
    }

    private fun guardarHorariosEnPrefs() {
        val editor = sharedPrefs.edit()
        for ((dia, textView) in horarioCompleto) {
            editor.putString(dia, textView.text.toString())
        }
        editor.apply()
    }

    private fun cargarHorariosDesdePrefs() {
        for (dia in ordenDias) {
            val contenido = sharedPrefs.getString(dia, null)
            if (contenido != null) {
                val sinTitulo = contenido.removePrefix("$dia\n")
                val textView = crearTextViewDia(dia, sinTitulo)
                horarioCompleto[dia] = textView
            }
        }
        actualizarLayout()
    }
}


