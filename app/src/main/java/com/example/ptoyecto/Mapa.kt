package com.example.ptoyecto

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.chrisbanes.photoview.PhotoView
import android.app.AlertDialog
import android.text.InputType
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Mapa : AppCompatActivity() {


    private lateinit var mapaCanvas: MapaCanvasView
    private lateinit var editTextOrigin: AutoCompleteTextView
    private lateinit var editTextDestination: AutoCompleteTextView
    private lateinit var btnFavoritos: ConstraintLayout
    private lateinit var btnGuardar: ConstraintLayout

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val favoriteLocations = mutableListOf<FavoriteLocation>()
    private val imagenesIds = listOf(
        R.drawable.piso_0,
        R.drawable.piso_1,
        R.drawable.piso_2,
        R.drawable.piso_3,
        R.drawable.piso_4,
        R.drawable.piso_5,
        R.drawable.piso_6,
        R.drawable.piso_7
    )
    var indiceActual: Int = 1

    class FavoriteLocation(
        val id: String = "",
        val name: String,
        val startNodeId: String,
        val endNodeId: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Si el usuario no está autenticado, redirigir al login
        if (auth.currentUser == null) {
            startActivity(Intent(this, Menu::class.java))
            finish()
            return
        }

        mapaCanvas = findViewById(R.id.mapaCanvas)
        val subirPisoBtn = findViewById<ImageButton>(R.id.subir_piso)
        val bajarPisoBtn = findViewById<ImageButton>(R.id.bajar_piso)
        val textFloorIndicator = findViewById<TextView>(R.id.indicaorPiso)


        fun actualizarMapa() {
            val options = BitmapFactory.Options().apply {
                inSampleSize = 2
            }
            val bitmap = BitmapFactory.decodeResource(resources, imagenesIds[indiceActual], options)
            mapaCanvas.setMapaBitmap(bitmap)

            textFloorIndicator.text = "Piso ${indiceActual}"

        }
        actualizarMapa()
        val maxIndex = 7
        subirPisoBtn.setOnClickListener {
            if (indiceActual < maxIndex) {
                indiceActual += 1
                mapaCanvas.currentFloor = indiceActual
                actualizarMapa()
            }
        }

        bajarPisoBtn.setOnClickListener {
            if (indiceActual > 0) {
                indiceActual -= 1
                mapaCanvas.currentFloor = indiceActual
                actualizarMapa()
            }
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.piso_1, options)
        mapaCanvas.setMapaBitmap(bitmap)

        editTextOrigin = findViewById(R.id.search_bar2)
        editTextDestination = findViewById(R.id.search_bar)
        btnFavoritos = findViewById(R.id.btnFavoritos)
        btnGuardar = findViewById(R.id.btnGuardar)

        findViewById<Button>(R.id.buttonMenu).setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }

        setupAutoComplete()
        setupButtons()
        loadFavoriteLocationsFromFirestore()

        findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            Toast.makeText(this, "Funcionalidad de cámara en desarrollo", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnQuickAccess).setOnClickListener {
            showQuickAccessDialog()
        }

        findViewById<Button>(R.id.btnClearRoute).setOnClickListener {
            clearRoute()
        }

        findViewById<Button>(R.id.btnShowPathInfo).setOnClickListener {
            showPathInfo()
        }

        findViewById<ImageButton>(R.id.btnSearch).setOnClickListener {
            val query = editTextDestination.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query, false)
            }
        }

        findViewById<ImageButton>(R.id.btnLocateMe).setOnClickListener {
            val query = editTextOrigin.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query, true)
            } else {
                Toast.makeText(this, "Ingresa tu ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupButtons() {
        btnFavoritos.setOnClickListener {
            showFavoriteLocationsDialog()
        }
        btnGuardar.setOnClickListener {
            saveCurrentLocationToFirestore()
        }
    }


    private fun saveCurrentLocationToFirestore() {
        val startNode = mapaCanvas.startNode
        val endNode = mapaCanvas.endNode

        if (startNode == null || endNode == null) {
            Toast.makeText(this, "Primero selecciona un origen y un destino", Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Nombre para esta ruta"

        AlertDialog.Builder(this)
            .setTitle("Guardar ruta")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    // Crear un nuevo documento en Firestore
                    val userId = auth.currentUser?.uid ?: return@setPositiveButton

                    val newRoute = hashMapOf(
                        "name" to name,
                        "startNodeId" to startNode.id,
                        "endNodeId" to endNode.id,
                        "timestamp" to System.currentTimeMillis()
                    )


                    db.collection("usuarios")
                        .document(userId)
                        .collection("rutas")
                        .add(newRoute)
                        .addOnSuccessListener { documentReference ->
                            // Añadir a la lista local
                            val favoriteLocation = FavoriteLocation(
                                id = documentReference.id,
                                name = name,
                                startNodeId = startNode.id,
                                endNodeId = endNode.id
                            )
                            favoriteLocations.add(favoriteLocation)

                            Toast.makeText(this, "Ruta guardada como: $name", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar la ruta: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("Firestore", "Error saving route", e)
                        }
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Método para cargar rutas guardadas desde Firestore
    private fun loadFavoriteLocationsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("routes")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                favoriteLocations.clear()
                for (document in documents) {
                    val id = document.id
                    val name = document.getString("name") ?: continue
                    val startNodeId = document.getString("startNodeId") ?: continue
                    val endNodeId = document.getString("endNodeId") ?: continue
                    val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()

                    favoriteLocations.add(
                        FavoriteLocation(
                            id = id,
                            name = name,
                            startNodeId = startNodeId,
                            endNodeId = endNodeId,
                            timestamp = timestamp
                        )
                    )
                }
                Log.d("Firestore", "Loaded ${favoriteLocations.size} routes")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar rutas: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error loading routes", e)
            }
    }

    // Método para eliminar rutas de Firestore
    private fun deleteFavoriteRouteFromFirestore(favoriteLocation: FavoriteLocation) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("routes")
            .document(favoriteLocation.id)
            .delete()
            .addOnSuccessListener {
                favoriteLocations.remove(favoriteLocation)
                Toast.makeText(this, "Ruta eliminada: ${favoriteLocation.name}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar la ruta: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error deleting route", e)
            }
    }

    // Método modificado para eliminar múltiples rutas
    private fun deleteMultipleFavoriteRoutesFromFirestore(toRemove: List<FavoriteLocation>) {
        val userId = auth.currentUser?.uid ?: return
        val batch = db.batch()

        for (favorite in toRemove) {
            val routeRef = db.collection("users")
                .document(userId)
                .collection("routes")
                .document(favorite.id)
            batch.delete(routeRef)
        }

        batch.commit()
            .addOnSuccessListener {
                favoriteLocations.removeAll(toRemove)
                Toast.makeText(this, "${toRemove.size} rutas eliminadas", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar rutas: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error batch deleting routes", e)
            }
    }

    private fun showDeleteFavoritesDialog() {
        val items = favoriteLocations.map { it.name }.toTypedArray()
        val checkedItems = BooleanArray(items.size) { false }

        AlertDialog.Builder(this)
            .setTitle("Eliminar favoritos")
            .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Eliminar") { _, _ ->
                val toRemove = mutableListOf<FavoriteLocation>()
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        toRemove.add(favoriteLocations[i])
                    }
                }

                // Eliminar de Firestore
                deleteMultipleFavoriteRoutesFromFirestore(toRemove)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun setupAutoComplete() {
        if (!::mapaCanvas.isInitialized || mapaCanvas.currentNodes.isEmpty()) {
            Log.e("setupAutoComplete", "mapaCanvas no está listo aún.")
            return
        }
        val locationNames = mapaCanvas.currentNodes.map { it.name }
        val originAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationNames)
        val destinationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationNames)

        editTextOrigin.setAdapter(originAdapter)
        editTextDestination.setAdapter(destinationAdapter)

        editTextOrigin.setOnItemClickListener { _, _, position, _ ->
            val selectedName = originAdapter.getItem(position).toString()
            val selectedNode = mapaCanvas.currentNodes.find  { it.name == selectedName }
            selectedNode?.let {
                mapaCanvas.setStartNode(it.id.toString())

                centerMapOnNode(it)
            }
        }


        editTextDestination.setOnItemClickListener { _, _, position, _ ->
            val selectedName = destinationAdapter.getItem(position).toString()
            val selectedNode = mapaCanvas.currentNodes.find { it.name == selectedName }
            selectedNode?.let {
                mapaCanvas.setEndNode(it.id.toString())
                if (mapaCanvas.startNode != null) {
                    mapaCanvas.findPath()
                }
            }
        }
    }

    private fun centerMapOnNode(node: Node) {
        val nodeX = (node.x / 100f) * mapaCanvas.originalImageWidth
        val nodeY = (node.y / 100f) * mapaCanvas.originalImageHeight

        mapaCanvas.scrollTo(nodeX.toInt(), nodeY.toInt())
    }

    private fun searchLocation(query: String, isOrigin: Boolean) {
        val results = mapaCanvas.getNodesByName(query)

        if (results.isEmpty()) {
            Toast.makeText(this, "No se encontraron resultados para: $query", Toast.LENGTH_SHORT).show()
            return
        }

        if (results.size == 1) {
            val node = results.first()
            if (isOrigin) {
                mapaCanvas.setStartNode(node.id.toString())

                editTextOrigin.setText(node.name)
            } else {
                mapaCanvas.setEndNode(node.id)
                editTextDestination.setText(node.name)
            }
            centerMapOnNode(node)
        } else {
            showSearchResultsDialog(results, isOrigin)
        }
    }

    private fun showSearchResultsDialog(results: List<Node>, isOrigin: Boolean) {
        val items = results.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona una ubicación")
            .setItems(items) { _, which ->
                val selectedNode = results[which]
                if (isOrigin) {
                    mapaCanvas.setStartNode(selectedNode.id)
                    editTextOrigin.setText(selectedNode.name)
                } else {
                    mapaCanvas.setEndNode(selectedNode.id)
                    editTextDestination.setText(selectedNode.name)
                }
                centerMapOnNode(selectedNode)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showQuickAccessDialog() {
        val specialNodes = mapaCanvas.getSpecialNodes()
        val items = specialNodes.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Puntos de interés")
            .setItems(items) { _, which ->
                val selectedNode = specialNodes[which]
                showOriginDestinationDialog(selectedNode)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showOriginDestinationDialog(node: Node) {
        val options = arrayOf("Origen", "Destino")

        AlertDialog.Builder(this)
            .setTitle("Establecer como")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        mapaCanvas.setStartNode(node.id)
                        editTextOrigin.setText(node.name)
                    }
                    1 -> {
                        mapaCanvas.setEndNode(node.id)
                        editTextDestination.setText(node.name)
                    }
                }
                centerMapOnNode(node)
            }
            .show()
    }

    fun distanceBetween(startNode: Node, endNode: Node): Float {
        val dx = endNode.x - startNode.x
        val dy = endNode.y - startNode.y
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun showPathInfo() {
        val path = mapaCanvas.currentPath
        if (path.isEmpty()) {
            Toast.makeText(this, "No hay ruta establecida", Toast.LENGTH_SHORT).show()
            return
        }
        var totalDistance = 0f
        for (i in 0 until path.size - 1) {
            val startNode = path[i]
            val endNode = path[i + 1]
            totalDistance += distanceBetween(startNode, endNode)
        }

        val startNode = mapaCanvas.startNode
        val endNode = mapaCanvas.endNode

        val message = StringBuilder()
        message.append("Origen: ${startNode?.name ?: "No seleccionado"}\n")
        message.append("Destino: ${endNode?.name ?: "No seleccionado"}\n")
        message.append("Distancia: ${String.format("%.2f", totalDistance)} unidades\n")
        message.append("Puntos intermedios: ${path.size}\n\n")

        message.append("Recorrido:\n")
        path.forEachIndexed { index, node ->
            message.append("${index + 1}. ${node.name}\n")
        }

        AlertDialog.Builder(this)
            .setTitle("Información de la ruta")
            .setMessage(message.toString())
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun clearRoute() {
        mapaCanvas.resetPath()
        editTextOrigin.text.clear()
        editTextDestination.text.clear()
        Toast.makeText(this, "Ruta eliminada", Toast.LENGTH_SHORT).show()
    }

    private fun showFavoriteLocationsDialog() {
        if (favoriteLocations.isEmpty()) {
            Toast.makeText(this, "No tienes rutas favoritas guardadas", Toast.LENGTH_SHORT).show()
            return
        }

        val items = favoriteLocations.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Rutas favoritas")
            .setItems(items) { _, which ->
                val favorite = favoriteLocations[which]
                loadFavoriteLocation(favorite)
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Eliminar") { _, _ ->
                showDeleteFavoritesDialog()
            }
            .show()
    }

    private fun loadFavoriteLocation(favorite: FavoriteLocation) {
        mapaCanvas.setStartNode(favorite.startNodeId)
        mapaCanvas.setEndNode(favorite.endNodeId)

        val startNode = mapaCanvas.getNodeById(favorite.startNodeId)
        val endNode = mapaCanvas.getNodeById(favorite.endNodeId)

        if (startNode != null && endNode != null) {
            editTextOrigin.setText(startNode.name)
            editTextDestination.setText(endNode.name)

            mapaCanvas.findPath()

            fitRouteInView()
        }
    }

    private fun fitRouteInView() {
        val path = mapaCanvas.currentPath
        if (path.isEmpty()) return

        if (path.size >= 2) {
            val midIndex = path.size / 2
            centerMapOnNode(path[midIndex])
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Menu::class.java))
        finish()
    }
}