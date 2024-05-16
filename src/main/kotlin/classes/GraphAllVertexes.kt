package org.example.classes

import Point

class GraphAllVertexes(val points: List<Point>) : Graph() {

    init {
        points.forEach { vertexes.add(GraphVertex(it)) }
    }

}