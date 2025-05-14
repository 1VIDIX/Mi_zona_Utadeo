package com.example.ptoyecto

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.graphics.Paint
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.PointF
import kotlin.math.sqrt
import kotlin.math.pow
import android.graphics.*
import android.graphics.Matrix
import android.view.ScaleGestureDetector
import com.example.ptoyecto.nodosPorPiso
import com.example.ptoyecto.aristasPorPiso
import java.util.*

data class Node(val id: String, val x: Float, val y: Float, val name: String)
data class Edge(val from: String, val to: String, val weight: Float)

class MapaCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var drawMatrix: Matrix = Matrix()
    private var nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var edgePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var startNode: Node? = null
    var endNode: Node? = null
    var currentPath: List<Node> = emptyList()
    private var selectedNodeId: String? = null
    private var showAllNodeLabels = false
    private var selectingStartPoint = true
    private var mapaBitmap: Bitmap? = null
    private val matrixValues = FloatArray(9)
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private var scaleDetector: ScaleGestureDetector
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isPanning = false

    var currentFloor: Int = 1
        set(value) {
            field = value
            // Update nodes and edges when floor changes
            updateNodesAndEdges()
            // Reset path when floor changes
            resetPath()
            // Rebuild graph for new floor
            rebuildGraph()
            invalidate()
        }

    // Updated as properties that change with currentFloor
    var currentNodes: List<Node> = nodosPorPiso[currentFloor] ?: emptyList()
        private set
    var currentEdges: List<Edge> = aristasPorPiso[currentFloor] ?: emptyList()
        private set

    private var graph: Map<String, List<Pair<String, Float>>> = buildGraph()

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val focusX = detector.focusX
            val focusY = detector.focusY

            drawMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            invalidate()
            return true
        }
    }
    var originalImageWidth = 1f
    var originalImageHeight = 1f
    val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private var nodeRadius = 10f
    private var edgeThickness = 10f
    private var pathThickness = 10f

    init {
        scaleDetector = ScaleGestureDetector(context, ScaleListener())

        nodePaint.apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        edgePaint.apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = edgeThickness
        }

        pathPaint.apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = pathThickness
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        labelPaint.apply {
            color = Color.BLACK
            textSize = 12f
            textAlign = Paint.Align.CENTER
        }

        // Initialize nodes and edges with the default floor
        updateNodesAndEdges()
    }

    // Method to update nodes and edges for the current floor
    private fun updateNodesAndEdges() {
        currentNodes = nodosPorPiso[currentFloor] ?: emptyList()
        currentEdges = aristasPorPiso[currentFloor] ?: emptyList()
        Log.d("MapaCanvasView", "Floor changed to $currentFloor. Nodes count: ${currentNodes.size}")
    }

    // Method to rebuild the graph for the current floor
    private fun rebuildGraph() {
        graph = buildGraph()
    }

    fun setStartNode(nodeId: String?) {
        nodeId?.let { id ->
            startNode = currentNodes.find { it.id == id }
            if (startNode != null && endNode != null) {
                findPath()
            }
        }
        invalidate()
    }

    fun setEndNode(nodeId: String?) {
        nodeId?.let { id ->
            endNode = currentNodes.find { it.id == id }
            if (startNode != null && endNode != null) {
                findPath()
            }
        }
        invalidate()
    }

    fun findPath() {
        val start = startNode?.id ?: return
        val end = endNode?.id ?: return

        currentPath = dijkstra(start, end)
        invalidate()
    }

    private fun buildGraph(): Map<String, List<Pair<String, Float>>> {
        val graph = HashMap<String, MutableList<Pair<String, Float>>>()

        // Always use currentNodes and currentEdges which are updated with floor changes
        currentNodes.forEach { node ->
            graph[node.id] = mutableListOf()
        }

        currentEdges.forEach { edge ->
            graph[edge.from]?.add(Pair(edge.to, edge.weight))
            graph[edge.to]?.add(Pair(edge.from, edge.weight))
        }

        return graph
    }

    private fun dijkstra(start: String, end: String): List<Node> {
        val distances = HashMap<String, Float>()
        val previous = HashMap<String, String?>()
        val queue = PriorityQueue<Pair<String, Float>>(compareBy { it.second })

        currentNodes.forEach { node ->
            distances[node.id] = Float.POSITIVE_INFINITY
            previous[node.id] = null
        }
        distances[start] = 0f
        queue.add(Pair(start, 0f))

        while (queue.isNotEmpty()) {
            val (currentNodeId, _) = queue.poll()

            if (currentNodeId == end) break

            graph[currentNodeId]?.forEach { (neighborId, weight) ->
                val distance = distances[currentNodeId]!! + weight

                if (distance < distances[neighborId]!!) {
                    distances[neighborId] = distance
                    previous[neighborId] = currentNodeId
                    queue.add(Pair(neighborId, distance))
                }
            }
        }

        // Recupera el camino más corto
        val path = mutableListOf<Node>()
        var currentNodeId: String? = end
        while (currentNodeId != null) {
            val node = currentNodes.find { it.id == currentNodeId }
            node?.let { path.add(0, it) }
            currentNodeId = previous[currentNodeId]
        }

        return path
    }

    fun setMapaBitmap(bitmap: Bitmap) {
        mapaBitmap = bitmap
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isPanning = true

                // Solo intentamos seleccionar un nodo si no estamos en medio de un gesto de escala
                if (!scaleDetector.isInProgress) {
                    val closestNode = findClosestNode(event.x, event.y)

                    if (closestNode != null) {
                        // Selección de nodo detectada
                        if (selectingStartPoint) {
                            startNode = closestNode
                            selectingStartPoint = false
                            Log.d("MapaCanvasView", "Nodo inicio seleccionado: ${closestNode.id}")
                        } else {
                            endNode = closestNode
                            selectingStartPoint = true
                            Log.d("MapaCanvasView", "Nodo fin seleccionado: ${closestNode.id}")

                            if (startNode != null && endNode != null) {
                                findPath()
                            }
                        }
                        invalidate()
                        return true
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isPanning && !scaleDetector.isInProgress) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    drawMatrix.postTranslate(dx, dy)
                    invalidate()
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isPanning = false
            }
        }

        // Tocar para seleccionar nodos
        if (event.action == MotionEvent.ACTION_DOWN && !scaleDetector.isInProgress) {
            val x = event.x
            val y = event.y

            val invertedMatrix = Matrix()
            if (drawMatrix.invert(invertedMatrix)) {
                val mapPoint = floatArrayOf(x, y)
                invertedMatrix.mapPoints(mapPoint)

                val touchX = mapPoint[0]
                val touchY = mapPoint[1]

                val closestNode = findClosestNode(touchX, touchY)

                if (closestNode != null) {
                    if (selectingStartPoint) {
                        startNode = closestNode
                        selectingStartPoint = false
                    } else {
                        endNode = closestNode
                        selectingStartPoint = true

                        if (startNode != null && endNode != null) {
                            findPath()
                        }
                    }
                    invalidate()
                    return true
                }
            }
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return true
    }


    private fun findClosestNode(touchX: Float, touchY: Float): Node? {

        val values = FloatArray(9)
        drawMatrix.getValues(values)
        val scaleX = values[Matrix.MSCALE_X]

        val scaleFactor = 900f

        var closestNode: Node? = null
        var minDistance = Float.MAX_VALUE


        val invertedMatrix = Matrix()
        drawMatrix.invert(invertedMatrix)

        // Convertir punto de toque a espacio de coordenadas original
        val touchPoint = floatArrayOf(touchX, touchY)
        invertedMatrix.mapPoints(touchPoint)

        val mappedTouchX = touchPoint[0]
        val mappedTouchY = touchPoint[1]


        for (node in currentNodes) {

            val nodeX = node.x * originalImageWidth / 100f * scaleFactor
            val nodeY = node.y * originalImageHeight / 100f * scaleFactor


            Log.d("MapaCanvasView", "Nodo ${node.id} en: ($nodeX, $nodeY)")


            val distance = sqrt((nodeX - mappedTouchX).pow(2) + (nodeY - mappedTouchY).pow(2))

            if (distance < minDistance) {
                minDistance = distance
                closestNode = node
            }
        }

        val detectionRadius = nodeRadius * 5 / scaleX

        Log.d("MapaCanvasView", "Nodo más cercano: ${closestNode?.id}, distancia: $minDistance, umbral: $detectionRadius")

        return if (minDistance < detectionRadius) closestNode else null
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.save()
        canvas.concat(drawMatrix)

        mapaBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        val values = FloatArray(9)
        drawMatrix.getValues(values)
        val scaleX = values[Matrix.MSCALE_X]
        val scaleFactor = 900f

        val adjustedNodeRadius = nodeRadius / scaleX
        val adjustedEdgeThickness = edgeThickness / scaleX
        val adjustedPathThickness = pathThickness / scaleX
        val adjustedTextSize = 30f / scaleX

        edgePaint.strokeWidth = adjustedEdgeThickness
        pathPaint.strokeWidth = adjustedPathThickness
        labelPaint.textSize = adjustedTextSize


        for (edge in currentEdges) {
            val from = currentNodes.find { it.id == edge.from }
            val to = currentNodes.find { it.id == edge.to }
            if (from != null && to != null) {
                val x1 = from.x * originalImageWidth / 100f * scaleFactor
                val y1 = from.y * originalImageHeight / 100f * scaleFactor
                val x2 = to.x * originalImageWidth / 100f * scaleFactor
                val y2 = to.y * originalImageHeight / 100f * scaleFactor
                canvas.drawLine(x1, y1, x2, y2, edgePaint)
            }
        }

        // Dibuja el camino actual (path)
        for (i in 0 until currentPath.size - 1) {
            val from = currentPath[i]
            val to = currentPath[i + 1]
            val x1 = from.x * originalImageWidth / 100f * scaleFactor
            val y1 = from.y * originalImageHeight / 100f * scaleFactor
            val x2 = to.x * originalImageWidth / 100f * scaleFactor
            val y2 = to.y * originalImageHeight / 100f * scaleFactor
            canvas.drawLine(x1, y1, x2, y2, pathPaint)
        }

        // Dibuja los nodos
        for (node in currentNodes) {
            val x = node.x * originalImageWidth / 100f * scaleFactor
            val y = node.y * originalImageHeight / 100f * scaleFactor

            // Color especial si es inicio o fin
            when (node) {
                startNode -> nodePaint.color = Color.GREEN
                endNode -> nodePaint.color = Color.MAGENTA
                else -> nodePaint.color = Color.RED
            }

            canvas.drawCircle(x, y, adjustedNodeRadius, nodePaint)

            if (showAllNodeLabels || node.id == selectedNodeId || node == startNode || node == endNode) {

                canvas.drawText(node.name, x, y - 10f / scaleX, labelPaint)
            }
        }

        canvas.restoreToCount(saveCount)
    }


    fun toggleNodeLabels() {
        showAllNodeLabels = !showAllNodeLabels
        invalidate()
    }

    fun resetPath() {
        startNode = null
        endNode = null
        currentPath = emptyList()
        selectingStartPoint = true
        invalidate()
    }

    fun getNodesByName(query: String): List<Node> {
        return currentNodes.filter {
            it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
        }
    }

    fun getNodeById(id: String): Node? {
        return currentNodes.find { it.id == id }
    }

    fun getSpecialNodes(): List<Node> {
        return currentNodes.filter {
            it.id.startsWith("Escaleras") || it.id.contains("Baño")
        }
    }
}