package com.example.ptoyecto

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FormularioEditarDialog : DialogFragment() {

    private val auth = FirebaseAuth.getInstance()
    private var argumentosIniciales: Bundle? = null
    var listener: OnFormularioHorarioListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_formulario_editar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerDias = view.findViewById<Spinner>(R.id.spinnerDia)
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        spinnerDias.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diasSemana).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val inputCurso = view.findViewById<EditText>(R.id.inputCurso)
        val inputAula = view.findViewById<EditText>(R.id.inputAula)
        val inputProfesor = view.findViewById<EditText>(R.id.inputProfesor)
        val inputInicio = view.findViewById<TextView>(R.id.inputInicio)
        val inputFin = view.findViewById<TextView>(R.id.inputFin)

        var idClase: String? = null

        argumentosIniciales?.let { args ->
            inputCurso.setText(args.getString("curso", ""))
            inputAula.setText(args.getString("aula", ""))
            inputProfesor.setText(args.getString("profesor", ""))
            inputInicio.setText(args.getString("inicio", ""))
            inputFin.setText(args.getString("fin", ""))
            val dia = args.getString("dia", "")
            val index = diasSemana.indexOf(dia)
            if (index >= 0) spinnerDias.setSelection(index)

            idClase = args.getString("idClase", null) // recibir id si lo hay
        }

        view.findViewById<TextView>(R.id.btnConfirmar).setOnClickListener {
            val curso = inputCurso.text.toString().trim()
            val aula = inputAula.text.toString().trim()
            val profesor = inputProfesor.text.toString().trim()
            val diaSeleccionado = spinnerDias.selectedItem.toString().trim()
            val inicio = inputInicio.text.toString().trim()
            val fin = inputFin.text.toString().trim()

            if (curso.isEmpty() || aula.isEmpty() || diaSeleccionado.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val user = auth.currentUser
                if (user != null) {
                    // Llamamos al listener para que lo maneje Horario.kt
                    val modo = "editar"
                    listener?.onFormularioHorarioConfirmado(
                        curso, aula, profesor, diaSeleccionado, inicio, fin, idClase, modo
                    )
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val btnEliminar = view.findViewById<TextView>(R.id.btnEliminar)
        btnEliminar.setOnClickListener {

            val db = FirebaseFirestore.getInstance()
            val user = auth.currentUser
            if (user != null && idClase != null) {
                db.collection("usuarios")
                    .document(user.uid)
                    .collection("horarios")
                    .document(idClase!!)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Clase eliminada", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EliminarClase", "Error al eliminar", e)
                        Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "No se puede eliminar", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<TextView>(R.id.btnCancelar).setOnClickListener {
            dismiss()
        }
    }

    fun setValoresIniciales(
        curso: String,
        aula: String,
        dia: String,
        inicio: String,
        fin: String,
        profesor: String,
        idClase: String? // nuevo parametro para edicion
    ) {
        argumentosIniciales = Bundle().apply {
            putString("curso", curso)
            putString("aula", aula)
            putString("dia", dia)
            putString("inicio", inicio)
            putString("fin", fin)
            putString("profesor", profesor)
            putString("idClase", idClase) // guardar id
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    interface OnFormularioHorarioListener {
        fun onFormularioHorarioConfirmado(
            curso: String,
            aula: String,
            profesor: String,
            dia: String,
            inicio: String,
            fin: String,
            idClaseExistente: String?, // ahora con id opcional
            modo: String
        )
    }
}

