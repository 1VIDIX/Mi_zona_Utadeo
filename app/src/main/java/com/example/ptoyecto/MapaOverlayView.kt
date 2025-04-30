package com.example.ptoyecto

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

data class Node(val id: String, val x: Float, val y: Float, val name: String)
data class Edge(val from: String, val to: String, val weight: Float)

class MapaOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawMatrix: Matrix = Matrix()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val nodes = listOf(
        Node("P1", 305f, 121f, "Pasillo 1"), Node("P2", 305f, 233f, "Pasillo 2"),
        Node("P3", 305f, 324f, "Pasillo 3"), Node("P4", 305f, 435f, "Pasillo 4"),
        Node("P5", 305f, 518f, "Pasillo 5"), Node("P6", 305f, 630f, "Pasillo 6"),
        Node("P7", 305f, 630f, "Pasillo 7"), Node("P8", 305f, 630f, "Pasillo 8"),
        Node("P9", 305f, 630f, "Pasillo 9"), Node("P10", 305f, 630f, "Pasillo 10"),
        Node("P11", 305f, 630f, "Pasillo 11"), Node("P12", 705f, 630f, "Pasillo 12"),
        Node("Escalera", 910f, 630f, "Escaleras"),
        Node("Salon1", 340f, 53f, "Salón 1"), Node("Salon2", 260f, 159f, "Salón 2"),
        Node("Salon3", 340f, 276f, "Salón 3"), Node("Salon4", 260f, 320f, "Salón 4"),
        Node("Salon5", 340f, 370f, "Salón 5"), Node("Salon6", 260f, 415f, "Salón 6"),
        Node("Salon7", 340f, 480f, "Salón 7"), Node("Salon8", 260f, 520f, "Salón 8"),
        Node("Salon9", 340f, 550f, "Salón 9"), Node("Salon10", 100f, 620f, "Baños")
    )

    private val edges = listOf(
        Edge("P1", "P2", 112f), Edge("P2", "P3", 91f), Edge("P3", "P4", 111f),
        Edge("P4", "P5", 83f), Edge("P5", "P6", 93f), Edge("P6", "P7", 94f),
        Edge("P7", "P8", 123f), Edge("P8", "P9", 131f), Edge("P9", "P10", 97f),
        Edge("P10", "P11", 110f), Edge("P11", "P12", 100f),
        Edge("P1", "Salon1", 36f), Edge("P2", "Salon2", 49f),
        Edge("P3", "Salon3", 49f), Edge("P4", "Salon4", 49f),
        Edge("P5", "Salon5", 49f), Edge("P6", "Salon6", 49f),
        Edge("P7", "Salon7", 49f), Edge("P8", "Salon8", 49f),
        Edge("P9", "Salon9", 48f), Edge("P10", "Salon10", 48f),
        Edge("P8", "Escalera", 143f)
    )

    fun updateMatrix(matrix: Matrix) {
        drawMatrix.set(matrix)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.concat(drawMatrix)


        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        for (edge in edges) {
            val fromNode = nodes.find { it.id == edge.from }
            val toNode = nodes.find { it.id == edge.to }
            if (fromNode != null && toNode != null) {
                canvas.drawLine(fromNode.x, fromNode.y, toNode.x, toNode.y, paint)
            }
        }


        paint.color = Color.RED
        paint.style = Paint.Style.FILL

        for (node in nodes) {
            canvas.drawCircle(node.x, node.y, 15f, paint)
        }
    }
}