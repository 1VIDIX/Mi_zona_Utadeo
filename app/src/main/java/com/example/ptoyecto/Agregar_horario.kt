
package com.example.ptoyecto

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.*
import androidx.core.graphics.drawable.toDrawable

class FormularioHorarioDialog : DialogFragment() {

    private var recordatorioActivo = false
    var listener: OnFormularioHorarioListener? = null

    // Bundle para valores iniciales
    private var argumentosIniciales: Bundle? = null

    interface OnFormularioHorarioListener {
        fun onFormularioHorarioConfirmado(curso: String, aula: String, dia: String, inicio: String, fin: String)
    }

    fun setValoresIniciales(curso: String, aula: String, dia: String, inicio: String, fin: String) {
        argumentosIniciales = Bundle().apply {
            putString("curso", curso)
            putString("aula", aula)
            putString("dia", dia)
            putString("inicio", inicio)
            putString("fin", fin)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_formulario_horario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val spinnerDias = view.findViewById<Spinner>(R.id.spinnerDia)
            val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diasSemana)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDias.adapter = adapter

            val inputCurso = view.findViewById<EditText>(R.id.inputCurso)
            val inputAula = view.findViewById<EditText>(R.id.inputAula)
            val inputInicio = view.findViewById<TextView>(R.id.inputInicio)
            val inputFin = view.findViewById<TextView>(R.id.inputFin)
            val inputProfesor = view.findViewById<EditText>(R.id.inputProfesor) // por si lo necesitas despues

            // PRELLENAR CAMPOS SI HAY VALORES
            argumentosIniciales?.let { args ->
                inputCurso.setText(args.getString("curso", ""))
                inputAula.setText(args.getString("aula", ""))
                inputInicio.setText(args.getString("inicio", ""))
                inputFin.setText(args.getString("fin", ""))
                val dia = args.getString("dia", "")
                val index = diasSemana.indexOf(dia)
                if (index >= 0) spinnerDias.setSelection(index)
            }

            // Confirmar
            view.findViewById<TextView>(R.id.btnConfirmar).setOnClickListener {
                val curso = inputCurso.text.toString().trim()
                val aula = inputAula.text.toString().trim()
                val diaSeleccionado = spinnerDias.selectedItem.toString().trim()
                val inicio = inputInicio.text.toString().trim()
                val fin = inputFin.text.toString().trim()

                if (curso.isEmpty() || aula.isEmpty() || diaSeleccionado.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    listener?.onFormularioHorarioConfirmado(curso, aula, diaSeleccionado, inicio, fin)
                    Toast.makeText(requireContext(), "Completado", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

            // Cancelar
            view.findViewById<TextView>(R.id.btnCancelar).setOnClickListener {
                dismiss()
            }

            // Recordatorio
            val btnRecordatorio = view.findViewById<TextView>(R.id.btnRecordatorio)
            actualizarColorBoton(btnRecordatorio)
            btnRecordatorio.setOnClickListener {
                recordatorioActivo = !recordatorioActivo
                actualizarColorBoton(btnRecordatorio)
            }

        } catch (e: Exception) {
            Log.e("FormularioDialog", "Error al crear formulario: ${e.message}")
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
            boton.setBackgroundResource(R.drawable.rounded_button_blue)
            boton.setTextColor(contexto.getColor(R.color.white))
        } else {
            boton.setBackgroundResource(R.drawable.button_gray)
            boton.setTextColor(contexto.getColor(R.color.black))
        }
    }
}
