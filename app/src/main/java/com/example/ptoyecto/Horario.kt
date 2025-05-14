package com.example.ptoyecto

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Horario : AppCompatActivity(), FormularioHorarioDialog.OnFormularioHorarioListener,
    FormularioEditarDialog.OnFormularioHorarioListener {

    private val ordenDias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    private lateinit var horarioLayout: LinearLayout
    private val horarioCompleto = mutableMapOf<String, MutableList<TextView>>()
    private val titulosDias = mutableMapOf<String, TextView>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario)

        horarioLayout = findViewById(R.id.linearLayoutHorarios)

        val welcomeTextView: TextView = findViewById(R.id.textView3)
        welcomeTextView.text = "Horario"

        findViewById<Button>(R.id.buttonMenu).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.boton_agregar).setOnClickListener {
            val formulario = FormularioHorarioDialog()
            formulario.listener = this
            formulario.show(supportFragmentManager, "FormularioHorario")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            Log.d("DEBUGGG", "Usuario no autenticado")
            return
        }

        Log.d("DEBUGGG", "Usuario autenticado con ID: $userId")
        cargarHorariosDesdeFirestore()
    }

    override fun onFormularioHorarioConfirmado(
        curso: String,
        aula: String,
        profesor: String,
        dia: String,
        inicio: String,
        fin: String,
        idClaseExistente: String?,
        modo: String
    ) {

        val horario = hashMapOf(
            "curso" to curso,
            "aula" to aula,
            "profesor" to if (profesor.isNotBlank()) profesor else "vacio",
            "inicio" to inicio,
            "fin" to fin
        )

        if (modo == "editar") {

            return
        }
        val idClase = idClaseExistente ?: db.collection("usuarios").document(userId)
            .collection("horarios").document(dia).collection("clases").document().id

        db.collection("usuarios").document(userId)
            .collection("horarios").document(dia)
            .collection("clases").document(idClase)
            .set(horario)
            .addOnSuccessListener {
                if (idClaseExistente != null) {
                    horarioCompleto[dia]?.removeAll { it.tag == idClase }
                }
                horarioCompleto.clear()
                titulosDias.clear()
                cargarHorariosDesdeFirestore()
            }
    }
    override fun onFormularioHorarioBorrado() {
        horarioCompleto.clear()
        titulosDias.clear()
        cargarHorariosDesdeFirestore()
    }


    private fun crearYMostrarTextView(dia: String, idClase: String, datos: Map<String, String>, modo: String = "crear") {

        val contenido = """
            Curso: ${datos["curso"]} 
            Aula: ${datos["aula"]} 
            Profesor: ${datos["profesor"]} 
            Inicio: ${datos["inicio"]} 
            Fin: ${datos["fin"]}
        """.trimIndent()

        if (modo == "editar") {
            val textViewExistente = horarioCompleto[dia]?.find { it.tag == idClase }
            textViewExistente?.let { tv ->
                tv.text = contenido
                tv.setOnClickListener {
                    val dialog = FormularioEditarDialog()
                    dialog.listener = this@Horario
                    dialog.setValoresIniciales(
                        datos["curso"] ?: "",
                        datos["aula"] ?: "",
                        dia,
                        datos["inicio"] ?: "",
                        datos["fin"] ?: "",
                        if (datos["profesor"] == "vacio") "" else datos["profesor"] ?: "",
                        idClase
                    )
                    dialog.show(supportFragmentManager, "FormularioHorario")
                }
                actualizarLayout()
            }
            return
        }

        if (!titulosDias.containsKey(dia)) {
            val tituloDia = TextView(this).apply {
                text = dia
                setBackgroundResource(R.drawable.info_horario)
                setTextColor(getColor(R.color.black))
                textSize = 18f
                setPadding(32, 32, 32, 32)

                val params = LinearLayout.LayoutParams(
                    (resources.displayMetrics.widthPixels * 0.8).toInt(), // 80% del ancho de pantalla
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0,-10,0,0)
            }
            titulosDias[dia] = tituloDia
        }

        val claseTextView = TextView(this).apply {
            text = contenido
            setTextColor(getColor(R.color.black))
            textSize = 16f
            setPadding(16, 32, 16, 16)
            setBackgroundResource(R.drawable.info_horario_contenido)

            tag = idClase

            // CENTRAR Y HACER MAS FLACO
            val params = LinearLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.8).toInt(), // 80% del ancho de pantalla
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.CENTER_HORIZONTAL
            params.setMargins(0, 16, 0, 16)
            layoutParams = params



            setOnClickListener {
                val dialog = FormularioEditarDialog()
                dialog.listener = this@Horario
                dialog.setValoresIniciales(
                    datos["curso"] ?: "",
                    datos["aula"] ?: "",
                    dia,
                    datos["inicio"] ?: "",
                    datos["fin"] ?: "",
                    if (datos["profesor"] == "vacio") "" else datos["profesor"] ?: "",
                    idClase
                )
                dialog.show(supportFragmentManager, "FormularioEditarDialog")
                actualizarLayout()
            }
        }

        if (horarioCompleto[dia] == null) {
            horarioCompleto[dia] = mutableListOf()
        }
        horarioCompleto[dia]?.add(claseTextView)
        actualizarLayout()
    }

    private fun actualizarLayout() {
        horarioLayout.removeAllViews()
        for (dia in ordenDias) {
            val clases = horarioCompleto[dia]
            if (!clases.isNullOrEmpty()) {
                titulosDias[dia]?.let { horarioLayout.addView(it) }
                clases.forEach { claseTextView ->
                    horarioLayout.addView(claseTextView)
                }
            }
        }
    }

    private fun cargarHorariosDesdeFirestore() {
        val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        for (dia in dias) {
            db.collection("usuarios").document(userId)
                .collection("horarios")
                .document(dia)
                .collection("clases")
                .get()
                .addOnSuccessListener { clases ->
                    for (clase in clases) {
                        val datos = clase.data.mapValues { it.value.toString() }
                        crearYMostrarTextView(dia, clase.id, datos)
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("DEBUGGG", "Error al obtener clases para el día $dia: ${e.message}")
                }
        }
    }
}