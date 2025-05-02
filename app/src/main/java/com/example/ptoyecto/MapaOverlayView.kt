package com.example.ptoyecto

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.sqrt

data class Node(val id: String, val x: Float, val y: Float, val name: String) {
    fun distanceTo(other: Node): Float {
        return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
    }
}

data class Edge(val from: String, val to: String, val weight: Float)

class MapaOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawMatrix: Matrix = Matrix()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
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

    val nodes = listOf(
        Node("P1", 45f, 175f, "Pasillo 1"),
        Node("P2", 60f, 175f, "Pasillo 2"),
        Node("P3", 70f, 175f, "Pasillo 3"),
        Node("P4", 80f, 175f, "Pasillo 4"),
        Node("P5", 90f, 175f, "Pasillo 5"),
        Node("P6", 100f, 175f, "Pasillo 6"),
        Node("P7", 110f, 175f, "Pasillo 7"),
        Node("P8", 120f, 175f, "Pasillo 8"),
        Node("P9", 130f, 175f, "Pasillo 9"),
        Node("P10", 140f, 175f, "Pasillo 10"),
        Node("P11", 150f, 175f, "Pasillo 11"),
        Node("P12", 160f, 175f, "Pasillo 12"),
        Node("P13", 170f, 175f, "Pasillo 13"),
        Node("P14", 180f, 175f, "Pasillo 14"),
        Node("P15", 190f, 175f, "Pasillo 15"),
        Node("P16", 200f, 175f, "Pasillo 16"),
        Node("P17", 210f, 175f, "Pasillo 17"),
        Node("P18", 220f, 175f, "Pasillo 18"),
        Node("P19", 230f, 175f, "Pasillo 19"),
        Node("P20", 240f, 175f, "Pasillo 20"),
        Node("P21", 250f, 175f, "Pasillo 21"),
        Node("P22", 255f, 175f, "Pasillo 22"),

        Node("N1", 40f, 148f, "Aula 1"),
        Node("N2", 66f, 168f, "Aula 4"),
        Node("N5", 87f, 168f, "Aula 6"),
        Node("N6", 107f, 168f, "Aula 8"),
        Node("N7", 113f, 168f, "Aula 9"),
        Node("N10", 145f, 168f, "Aula 11"),
        Node("N13", 168f, 170f, "Aula 12"),
        Node("N16", 204f, 170f, "Aula 15"),
        Node("N18", 219f, 170f, "Aula 17"),
        Node("N19", 232f, 170f, "Aula 19"),
        Node("N20", 246f, 170f, "Aula 21"),

        Node("S1", 42f, 183f, "Aula 2"),
        Node("S2", 56f, 183f, "Aula 3"),
        Node("S3", 77f, 183f, "Aula 5"),
        Node("S5", 98f, 183f, "Aula 7"),
        Node("S7", 118f, 183f, "Aula 10"),
        Node("S14", 184f, 183f, "Aula 13"),
        Node("S15", 194f, 183f, "Aula 14"),
        Node("S16", 209f, 183f, "Aula 16"),
        Node("S18", 224f, 183f, "Aula 18"),
        Node("S20", 240f, 183f, "Aula 20"),

        Node("Escaleras 1", 45f, 70f, "Escaleras 1"),
        Node("Escaleras intermedias", 170f, 183f, "Escaleras intermedias"),
        Node("Baños mod7", 255f, 195f, "Baños mod7"),
        Node("Escaleras 3", 255f, 90f, "Escaleras 3")
    )

    private val edges = listOf(
        Edge("P1", "P2", 15f),
        Edge("P2", "P3", 10f),
        Edge("P3", "P4", 10f),
        Edge("P4", "P5", 10f),
        Edge("P5", "P6", 10f),
        Edge("P6", "P7", 10f),
        Edge("P7", "P8", 10f),
        Edge("P8", "P9", 10f),
        Edge("P9", "P10", 10f),
        Edge("P10", "P11", 10f),
        Edge("P11", "P12", 10f),
        Edge("P12", "P13", 10f),
        Edge("P13", "P14", 10f),
        Edge("P14", "P15", 10f),
        Edge("P15", "P16", 10f),
        Edge("P16", "P17", 10f),
        Edge("P17", "P18", 10f),
        Edge("P18", "P19", 10f),
        Edge("P19", "P20", 10f),
        Edge("P20", "P21", 10f),
        Edge("P21", "P22", 5f),

        Edge("P1", "N1", 27f),
        Edge("P2", "N2", 7f),
        Edge("P5", "N5", 7f),
        Edge("P6", "N6", 7f),
        Edge("P7", "N7", 7f),
        Edge("P10", "N10", 7f),
        Edge("P13", "N13", 5f),
        Edge("P16", "N16", 5f),
        Edge("P18", "N18", 5f),
        Edge("P19", "N19", 5f),
        Edge("P20", "N20", 5f),

        Edge("P1", "S1", 8f),
        Edge("P2", "S2", 8f),
        Edge("P3", "S3", 8f),
        Edge("P5", "S5", 8f),
        Edge("P7", "S7", 8f),
        Edge("P14", "S14", 8f),
        Edge("P15", "S15", 8f),
        Edge("P16", "S16", 8f),
        Edge("P18", "S18", 8f),
        Edge("P20", "S20", 8f),

        Edge("P1", "Escaleras 1", 105f),
        Edge("P13", "Escaleras intermedias", 8f),
        Edge("P22", "Baños mod7", 20f),
        Edge("P22", "Escaleras 3", 85f)
    )

    var originalImageWidth = 1f
    var originalImageHeight = 1f

    private var viewWidth = 1f
    private var viewHeight = 1f

    private var nodeRadius = 5f
    private var edgeThickness = 3f
    private var pathThickness = 5f

    private val graph: Map<String, List<Pair<String, Float>>> by lazy {
        buildGraph()
    }

    init {
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
    }

    fun updateMatrix(matrix: Matrix) {
        drawMatrix.set(matrix)
        invalidate()
    }

    fun setOriginalImageDimensions(width: Float, height: Float) {
        originalImageWidth = width
        originalImageHeight = height
        invalidate()
    }

    fun setStartNode(nodeId: String?) {
        nodeId?.let { id ->
            startNode = nodes.find { it.id == id }
            if (startNode != null && endNode != null) {
                findPath()
            }
        }
        invalidate()
    }

    fun setEndNode(nodeId: String?) {
        nodeId?.let { id ->
            endNode = nodes.find { it.id == id }
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

        nodes.forEach { node ->
            graph[node.id] = mutableListOf()
        }

        edges.forEach { edge ->
            graph[edge.from]?.add(Pair(edge.to, edge.weight))
            graph[edge.to]?.add(Pair(edge.from, edge.weight))
        }

        return graph
    }

    private fun dijkstra(start: String, end: String): List<Node> {
        val distances = HashMap<String, Float>()
        val previous = HashMap<String, String?>()
        val queue = PriorityQueue<Pair<String, Float>>(compareBy { it.second })

        nodes.forEach { node ->
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

        val path = mutableListOf<String>()
        var current: String? = end

        while (current != null) {
            path.add(0, current)
            current = previous[current]
        }

        if (path.firstOrNull() != start) return emptyList()

        return path.mapNotNull { nodeId ->
            nodes.find { it.id == nodeId }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
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

        return super.onTouchEvent(event)
    }

    private fun findClosestNode(x: Float, y: Float): Node? {
        val scaledX = x / originalImageWidth * 100f
        val scaledY = y / originalImageHeight * 100f

        var closestNode: Node? = null
        var minDistance = Float.MAX_VALUE

        for (node in nodes) {
            val distance = sqrt((node.x - scaledX).pow(2) + (node.y - scaledY).pow(2))
            if (distance < minDistance) {
                minDistance = distance
                closestNode = node
            }
        }

        return if (minDistance < 10f) closestNode else null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.save()

        canvas.concat(drawMatrix)

        val values = FloatArray(9)
        drawMatrix.getValues(values)
        val scale = values[Matrix.MSCALE_X]

        val adjustedNodeRadius = nodeRadius / scale
        val adjustedEdgeThickness = edgeThickness / scale
        val adjustedPathThickness = pathThickness / scale
        val adjustedTextSize = 12f / scale

        edgePaint.strokeWidth = adjustedEdgeThickness
        pathPaint.strokeWidth = adjustedPathThickness
        labelPaint.textSize = adjustedTextSize

        for (edge in edges) {
            val fromNode = nodes.find { it.id == edge.from }
            val toNode = nodes.find { it.id == edge.to }
            if (fromNode != null && toNode != null) {
                val fromX = (fromNode.x / 100f) * originalImageWidth
                val fromY = (fromNode.y / 100f) * originalImageHeight
                val toX = (toNode.x / 100f) * originalImageWidth
                val toY = (toNode.y / 100f) * originalImageHeight

                canvas.drawLine(fromX, fromY, toX, toY, edgePaint)
            }
        }

        if (currentPath.size > 1) {
            val path = Path()
            val firstNode = currentPath.first()
            val startX = (firstNode.x / 100f) * originalImageWidth
            val startY = (firstNode.y / 100f) * originalImageHeight

            path.moveTo(startX, startY)

            for (i in 1 until currentPath.size) {
                val node = currentPath[i]
                val x = (node.x / 100f) * originalImageWidth
                val y = (node.y / 100f) * originalImageHeight
                path.lineTo(x, y)
            }

            canvas.drawPath(path, pathPaint)
        }

        for (node in nodes) {
            val x = (node.x / 100f) * originalImageWidth
            val y = (node.y / 100f) * originalImageHeight

            when (node) {
                startNode -> {
                    nodePaint.color = Color.GREEN
                    canvas.drawCircle(x, y, adjustedNodeRadius * 1.5f, nodePaint)
                    nodePaint.color = Color.RED
                }
                endNode -> {
                    nodePaint.color = Color.MAGENTA
                    canvas.drawCircle(x, y, adjustedNodeRadius * 1.5f, nodePaint)
                    nodePaint.color = Color.RED
                }
                else -> {
                    if (currentPath.contains(node)) {
                        nodePaint.color = Color.GREEN
                        canvas.drawCircle(x, y, adjustedNodeRadius * 1.2f, nodePaint)
                        nodePaint.color = Color.RED
                    } else {
                        canvas.drawCircle(x, y, adjustedNodeRadius, nodePaint)
                    }
                }
            }

            if (node == startNode || node == endNode ||
                node.id.startsWith("Escaleras") || node.id.contains("Baños") ||
                showAllNodeLabels) {

                canvas.drawText(node.name, x, y - adjustedNodeRadius - 5, labelPaint)
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
        return nodes.filter {
            it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
        }
    }

    fun getNodeById(id: String): Node? {
        return nodes.find { it.id == id }
    }

    fun getSpecialNodes(): List<Node> {
        return nodes.filter {
            it.id.startsWith("Escaleras") || it.id.contains("Baños")
        }
    }

}