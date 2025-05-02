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
import android.widget.TextView
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import android.view.View
import android.content.DialogInterface

data class FavoriteLocation(
    val name: String,
    val startNodeId: String,
    val endNodeId: String
)

class Mapa : AppCompatActivity() {

    private lateinit var mapaCampus: PhotoView
    private lateinit var mapaOverlay: MapaOverlayView
    private lateinit var editTextOrigin: AutoCompleteTextView
    private lateinit var editTextDestination: AutoCompleteTextView
    private lateinit var btnFavoritos: ConstraintLayout
    private lateinit var btnGuardar: ConstraintLayout

    private val favoriteLocations = mutableListOf<FavoriteLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        mapaCampus = findViewById(R.id.mapaCampus)
        mapaOverlay = findViewById(R.id.mapaOverlay)
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

        mapaCampus.post {
            val drawable = mapaCampus.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val imageWidth = bitmap.width.toFloat()
                val imageHeight = bitmap.height.toFloat()
                mapaOverlay.setOriginalImageDimensions(imageWidth, imageHeight)
            }

            mapaCampus.setScale(1.0f, false)
            mapaCampus.setMinimumScale(0.5f)
            mapaCampus.setMaximumScale(3.0f)
            mapaCampus.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE)
        }

        mapaCampus.setOnMatrixChangeListener { rect ->
            mapaOverlay.updateMatrix(mapaCampus.imageMatrix)
        }
        setupMapInteraction()

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

    private fun setupAutoComplete() {
        val locationNames = mapaOverlay.nodes.map { it.name }
        val originAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationNames)
        val destinationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locationNames)

        editTextOrigin.setAdapter(originAdapter)
        editTextDestination.setAdapter(destinationAdapter)

        editTextOrigin.setOnItemClickListener { _, _, position, _ ->
            val selectedName = originAdapter.getItem(position).toString()
            val selectedNode = mapaOverlay.nodes.find { it.name == selectedName }
            selectedNode?.let {
                mapaOverlay.setStartNode(it.id)
                centerMapOnNode(it)
            }
        }

        // Manejar la selección del destino
        editTextDestination.setOnItemClickListener { _, _, position, _ ->
            val selectedName = destinationAdapter.getItem(position).toString()
            val selectedNode = mapaOverlay.nodes.find { it.name == selectedName }
            selectedNode?.let {
                mapaOverlay.setEndNode(it.id)
                if (mapaOverlay.startNode != null) {
                    mapaOverlay.findPath()
                }
            }
        }
    }

    private fun setupButtons() {
        btnFavoritos.setOnClickListener {
            showFavoriteLocationsDialog()
        }
        btnGuardar.setOnClickListener {
            saveCurrentLocation()
        }
    }

    private fun setupMapInteraction() {
        Toast.makeText(this, "Toca el mapa para seleccionar origen, luego toca de nuevo para el destino",
            Toast.LENGTH_LONG).show()
    }
    private fun centerMapOnNode(node: Node) {
        val nodeX = (node.x / 100f) * mapaOverlay.originalImageWidth
        val nodeY = (node.y / 100f) * mapaOverlay.originalImageHeight

        mapaCampus.setScale(1.5f, nodeX, nodeY, true)
    }

    private fun searchLocation(query: String, isOrigin: Boolean) {
        val results = mapaOverlay.getNodesByName(query)

        if (results.isEmpty()) {
            Toast.makeText(this, "No se encontraron resultados para: $query", Toast.LENGTH_SHORT).show()
            return
        }

        if (results.size == 1) {
            val node = results.first()
            if (isOrigin) {
                mapaOverlay.setStartNode(node.id)
                editTextOrigin.setText(node.name)
            } else {
                mapaOverlay.setEndNode(node.id)
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
                    mapaOverlay.setStartNode(selectedNode.id)
                    editTextOrigin.setText(selectedNode.name)
                } else {
                    mapaOverlay.setEndNode(selectedNode.id)
                    editTextDestination.setText(selectedNode.name)
                }
                centerMapOnNode(selectedNode)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showQuickAccessDialog() {
        val specialNodes = mapaOverlay.getSpecialNodes()
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
                        mapaOverlay.setStartNode(node.id)
                        editTextOrigin.setText(node.name)
                    }
                    1 -> {
                        mapaOverlay.setEndNode(node.id)
                        editTextDestination.setText(node.name)
                    }
                }
                centerMapOnNode(node)
            }
            .show()
    }

    private fun showPathInfo() {
        val path = mapaOverlay.currentPath
        if (path.isEmpty()) {
            Toast.makeText(this, "No hay ruta establecida", Toast.LENGTH_SHORT).show()
            return
        }
        var totalDistance = 0f
        for (i in 0 until path.size - 1) {
            totalDistance += path[i].distanceTo(path[i + 1])
        }
        val startNode = mapaOverlay.startNode
        val endNode = mapaOverlay.endNode

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
        mapaOverlay.resetPath()
        editTextOrigin.text.clear()
        editTextDestination.text.clear()
        Toast.makeText(this, "Ruta eliminada", Toast.LENGTH_SHORT).show()
    }

    private fun saveCurrentLocation() {
        val startNode = mapaOverlay.startNode
        val endNode = mapaOverlay.endNode

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
                    favoriteLocations.add(FavoriteLocation(name, startNode.id, endNode.id))
                    Toast.makeText(this, "Ruta guardada como: $name", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
                favoriteLocations.removeAll(toRemove)
                Toast.makeText(this, "Favoritos eliminados", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun loadFavoriteLocation(favorite: FavoriteLocation) {
        mapaOverlay.setStartNode(favorite.startNodeId)
        mapaOverlay.setEndNode(favorite.endNodeId)

        val startNode = mapaOverlay.getNodeById(favorite.startNodeId)
        val endNode = mapaOverlay.getNodeById(favorite.endNodeId)

        if (startNode != null && endNode != null) {
            editTextOrigin.setText(startNode.name)
            editTextDestination.setText(endNode.name)

            mapaOverlay.findPath()

            fitRouteInView()
        }
    }

    // Método para ajustar la vista para mostrar toda la ruta
    private fun fitRouteInView() {
        val path = mapaOverlay.currentPath
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