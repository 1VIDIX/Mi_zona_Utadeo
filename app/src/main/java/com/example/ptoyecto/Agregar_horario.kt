package com.example.ptoyecto

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.ptoyecto.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable

class FormularioHorarioDialog : DialogFragment() {
    private var recordatorioActivo = false
    var listener: OnFormularioHorarioListener? = null // Establece el listener

    interface OnFormularioHorarioListener {
        fun onFormularioHorarioConfirmado(curso: String, aula: String, dia: String, inicio: String, fin: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable()) // Mantiene el fondo transparente
        return dialog
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_formulario_horario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Spinner para seleccionar el día
            val spinnerDias = view.findViewById<Spinner>(R.id.spinnerDia)
            val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diasSemana)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDias.adapter = adapter

            // Botón confirmar
            val btnConfirmar = view.findViewById<TextView>(R.id.btnConfirmar)
            btnConfirmar.setOnClickListener {
                val curso = view.findViewById<EditText>(R.id.inputCurso).text.toString().trim()
                val aula = view.findViewById<EditText>(R.id.inputAula).text.toString().trim()
                val spinnerDia = spinnerDias.selectedItem.toString().trim() // Obtener el día seleccionado
                val profesor = view.findViewById<EditText>(R.id.inputProfesor).text.toString().trim() // Obtener el profesor seleccionado
                val inicio = view.findViewById<TextView>(R.id.inputInicio).text.toString().trim()
                val fin = view.findViewById<TextView>(R.id.inputFin).text.toString().trim()

                // Validación de campos vacíos
                if (curso.isEmpty() || aula.isEmpty() || spinnerDia.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    // Llamar al listener para pasar los datos
                    listener?.onFormularioHorarioConfirmado(curso, aula, spinnerDia, inicio, fin)
                    Toast.makeText(requireContext(), "Completado", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

            // Botón cancelar
            val btnCancelar = view.findViewById<TextView>(R.id.btnCancelar)
            btnCancelar.setOnClickListener {
                dismiss()
            }

            // Botón recordatorio
            val btnRecordatorio = view.findViewById<TextView>(R.id.btnRecordatorio)
            // Establecer el color inicial
            actualizarColorBoton(btnRecordatorio)

            btnRecordatorio.setOnClickListener {
                recordatorioActivo = !recordatorioActivo // Alternar el estado del recordatorio
                actualizarColorBoton(btnRecordatorio)
            }

        } catch (e: Exception) {
            Log.e("FormularioDialog", "Error al acceder a btnConfirmar: ${e.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun actualizarColorBoton(boton: TextView) {
        val contexto = boton.context
        if (recordatorioActivo) {
            boton.setBackgroundResource(R.drawable.rounded_button_blue) // Cambiar el color del botón
            boton.setTextColor(contexto.getColor(R.color.white)) // Cambiar color del texto
        } else {
            boton.setBackgroundResource(R.drawable.button_gray) // Color del botón cuando está inactivo
            boton.setTextColor(contexto.getColor(R.color.black)) // Color del texto cuando está inactivo
        }
    }
}
