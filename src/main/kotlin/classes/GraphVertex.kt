package org.example.classes

import Point

class GraphVertex(val point: Point)  {

    var marked = false
    val links = mutableListOf<LinkType>()
    fun connectPoint(point: Point): GraphVertex {
        val newVertex = GraphVertex(point)
        val weight = this.point.distance(point)
        newVertex.links.add(LinkType(this, weight))
        this.links.add(LinkType(newVertex, weight))
        return newVertex
    }

    fun connectToVertex(other: GraphVertex) {
        if (isLinkedTo(other) || other.isLinkedTo(this)) {
//            println(this.point.vNum)
//            println(other.point.vNum)
//            println(this)
//            println(other)
            throw Exception("cant connect")
        }
        val weight = this.point.distance(other.point)
        this.links.add(LinkType(other, weight))
        other.links.add(LinkType(this, weight))
    }

    fun detachVertex(other: GraphVertex) {
        this.links.removeIf { it.to.point == other.point }
        other.links.removeIf { it.to.point == this.point }
    }

    override fun toString(): String {
        return "GraphVertex(point=$point, marked=$marked, links=${links.map { it.to.point.vNum }})"
    }

    fun isLinkedTo(other: GraphVertex): Boolean {
        return this.links.any { it.to == other }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GraphVertex

        return point.vNum == other.point.vNum
    }

    override fun hashCode(): Int {
        return point.hashCode()
    }

    fun distance(v: GraphVertex): Int {
        if (this.point.vNum == v.point.vNum) throw Exception("Distance to itself")
        return this.point.distance(v.point)
    }

    fun cloneWithoutLinks(): GraphVertex {
        return GraphVertex(this.point)
    }

    val degree: Int
        get() = this.links.size

}

data class LinkType(val to: GraphVertex, val weight: Int)