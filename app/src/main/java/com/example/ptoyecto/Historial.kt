package com.example.ptoyecto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale


class HistorialActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnToggle: ImageButton
    private lateinit var fondo: LinearLayout
    private lateinit var textTitulo: TextView
    private lateinit var historialTextView: TextView
    private lateinit var historialRecyclerView: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var listaSugerencias: MutableList<Sugerencia> = mutableListOf()
    private lateinit var firestoreListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // Inicializar vistas
        btnBack = findViewById(R.id.back_button)
        btnToggle = findViewById(R.id.btnToggle)
        fondo = findViewById(R.id.fondo)
        textTitulo = findViewById(R.id.textTitulo)
        historialTextView = findViewById(R.id.historial_text_view)
        historialRecyclerView = findViewById(R.id.historialRecyclerView)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Configurar RecyclerView
        historialRecyclerView.layoutManager = LinearLayoutManager(this)
        historialAdapter = HistorialAdapter(this, listaSugerencias)
        historialRecyclerView.adapter = historialAdapter

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnToggle.setOnClickListener {
            fondo.visibility = if (fondo.visibility == LinearLayout.VISIBLE) LinearLayout.GONE else LinearLayout.VISIBLE
            btnToggle.setImageResource(if (fondo.visibility == LinearLayout.VISIBLE) R.drawable.ic_expand_more else R.drawable.ic_expand_more)
        }

        obtenerHistorialSugerencias()
    }

    private fun obtenerHistorialSugerencias() {
        val usuarioId = auth.currentUser?.uid
        if (usuarioId == null) {
            historialTextView.text = "Usuario no autenticado."
            historialRecyclerView.visibility = View.GONE
            return
        }

        firestoreListener = firestore.collection("sugerencias")
            .whereEqualTo("usuarioId", usuarioId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HistorialActivity", "Error al obtener historial: ${error.message}")
                    Toast.makeText(this, "Error al obtener el historial: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    listaSugerencias.clear()
                    for (document in snapshot) {
                        try {
                            val sugerencia = document.toObject(Sugerencia::class.java)
                            sugerencia?.let { // Manejar el caso de que toObject retorne null
                                listaSugerencias.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e("HistorialActivity", "Error al convertir documento: ${e.message}")
                            Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                    historialTextView.visibility = View.GONE
                    historialRecyclerView.visibility = View.VISIBLE
                    historialAdapter.notifyDataSetChanged()
                } else {
                    listaSugerencias.clear()
                    historialAdapter.notifyDataSetChanged()
                    historialTextView.text = "No hay sugerencias recientes."
                    historialTextView.visibility = View.VISIBLE
                    historialRecyclerView.visibility = View.GONE
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::firestoreListener.isInitialized) {
            firestoreListener.remove()
        }
    }

    data class Sugerencia(val mensaje: String? = null, val usuarioId: String? = null, val timestamp: Long? = null)
}

class HistorialAdapter(private val context: android.content.Context, private val listaSugerencias: List<HistorialActivity.Sugerencia>) :
    RecyclerView.Adapter<HistorialAdapter.SugerenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SugerenciaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_sugerencias, parent, false)
        return SugerenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SugerenciaViewHolder, position: Int) {
        val sugerencia = listaSugerencias[position]

        val formattedDate = sugerencia.timestamp?.let {
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(it)
        } ?: "Fecha no disponible"

        holder.sugerenciaTextView.text = "Sugerencia: ${sugerencia.mensaje ?: "N/A"}"

    }

    override fun getItemCount(): Int {
        return listaSugerencias.size
    }

    class SugerenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sugerenciaTextView: TextView = itemView.findViewById(R.id.sugerencia_edit_text)

    }
}