package org.example.classes

import org.example.utils.generateUniquePairs
import org.example.utils.shuffledWithinLimits
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.math.max
import kotlin.math.pow

open class Graph {

    protected val vertexes = mutableListOf<GraphVertex>()
    fun isValid(): Boolean {
        return vertexes.all { it.links.size == 3 }
    }

    val uniqLinks: List<LinkType>
        get() = vertexes.flatMap { v ->
            v.links.filter { link ->
                link.to.point.vNum < v.point.vNum
            }
        }


    fun saveEdgesToFile(filename: String) {
        val edges = vertexes.map { v ->
            v.links.filter { link -> link.to.point.vNum < v.point.vNum }
                .map { link -> Edge(link.to.point.vNum, v.point.vNum) }
        }.flatten()
        val bufferedWriter = BufferedWriter(FileWriter(File("output/$filename.txt")))
        bufferedWriter.write("c Вес кубического подграфа = $weight, самое длинное ребро = ${uniqLinks.maxOf { it.weight }}\np edge ${vertexes.size} ${(vertexes.size * 1.5).toInt()}\n")
        for (edge in edges) {
            bufferedWriter.write("e ${edge.first} ${edge.second}\n")
        }
        bufferedWriter.flush()
        bufferedWriter.close()
    }

    fun dumbFixDegrees() {
        val badVertexes = vertexes.filter { it.links.size != 3 }
        val edges = badVertexes.generateUniquePairs().sortedBy { it.first.distance(it.second) }
        val needMoreEdges = badVertexes.sumOf { 3 - it.degree }
        var added = 0
        for ((v1, v2) in edges) {
            if (v1.isLinkedTo(v2)) continue
            if (v1.degree >= 3 || v2.degree >= 3) continue
            v1.connectToVertex(v2)
            added += 2
            if (added == needMoreEdges) break
        }

        if (added != needMoreEdges) {
            val leftVertexes = vertexes.filter { it.links.size != 3 }
            val left = leftVertexes.size
            if (left == 1) {
                connect1lastPoint(leftVertexes)
            } else if (left == 2) {
                connect2lastPoint(leftVertexes)
            }
            if (!isValid()) {
                throw Exception("randomFixDegrees ERRROR")
            }
        }
    }

    fun searchForBestGraphBasedOnRandomFixDegrees(id: String) {
        var iterCount = 0
        val increaseShuffleStep = 2000
        var noUpgradeIterCount = 0
        var best = 111111111
        var bestEdges = this.randomFixDegrees(1)
        while (true) {
            try {
                iterCount++
                noUpgradeIterCount++
                val shuffleLimit = (noUpgradeIterCount * (1.0 / increaseShuffleStep)).pow(0.8).toInt() + 2
                if (shuffleLimit == 40) {
                    noUpgradeIterCount = 5 * increaseShuffleStep
                }
                if (iterCount % 100_000 == 0) {
                    println(listOf(iterCount, noUpgradeIterCount, shuffleLimit, best))
                }
                this.vertexes.forEach { it.links.clear() }
                val edges = fixDegreesByEdgesList(bestEdges.shuffledWithinLimits(shuffleLimit))
                if (weight < best) {
                    noUpgradeIterCount = 0
                    best = weight
                    println("$best $id")
                    bestEdges = edges
                    saveEdgesToFile(id)
                }
//                tryGetRidOfLongestEdges()
            } catch (_: Exception) {
            }
        }
    }

    fun randomFixDegrees(limit: Int): List<Pair<GraphVertex, GraphVertex>> {
        val badVertexes = vertexes.filter { it.links.size != 3 }
        var edges =
            badVertexes.generateUniquePairs().sortedBy { it.first.distance(it.second) }.shuffledWithinLimits(limit)
        edges = edges.subList(0, vertexes.size) + edges.subList(vertexes.size, edges.size).shuffledWithinLimits(limit)
//        println(edges.size)
        return fixDegreesByEdgesList(edges)
    }

    private fun fixDegreesByEdgesList(
        edges: List<Pair<GraphVertex, GraphVertex>>
    ): List<Pair<GraphVertex, GraphVertex>> {
        val needMoreEdges = vertexes.sumOf { max(3 - it.degree, 0) }
        var added = 0
        for ((v1, v2) in edges) {
            if (v1.isLinkedTo(v2)) continue
            if (v1.degree >= 3 || v2.degree >= 3) continue
            v1.connectToVertex(v2)
            added += 2
            if (added == needMoreEdges) break
        }

        if (added != needMoreEdges) {
            val leftVertexes = vertexes.filter { it.links.size != 3 }
            val left = leftVertexes.size
            if (left == 1) {
                connect1lastPoint(leftVertexes)
            } else if (left == 2) {
                connect2lastPoint(leftVertexes)
            }
            if (!isValid()) {
                throw Exception("randomFixDegrees ERRROR")
            }
        }
        return edges
    }

    fun loadEdgesFromFile(filePath: String) {
        val file = File(filePath)
        var lines = file.readLines()
        lines = lines.subList(2, lines.size)
        for (line in lines) {
            val arr = line.split(" ").subList(1, 3).map { it.toInt() }
            val v1 = vertexes.find { it.point.vNum == arr[0] }
            val v2 = vertexes.find { it.point.vNum == arr[1] }
            v1!!.connectToVertex(v2!!)
        }
    }

    fun fixVShapedEdges(id: String = "!") {

        while (true) {
            val edgesMap = vertexes.map { v ->
                v.links.map { link -> Pair(v, link.to) }
            }.flatten().groupBy { it.first }
//            println("enter tryGetRidOfLongestEdges iteration")
            var updatesCount = 0
            for (_edges in edgesMap.values) {
                val edges = _edges.sortedByDescending { it.first.distance(it.second) }.subList(0, 2)
                if (edges[0].second.isLinkedTo(edges[1].second)) continue
                var sumWas = edges.sumOf { it.first.distance(it.second) }
                val closestEdge = getClosestEdgeToVertex(edges[0].first)
                sumWas += closestEdge.first!!.distance(closestEdge.second!!)
                var newSum = edges[0].second.distance(edges[1].second)
                newSum += (edges[0].first.distance(closestEdge.first!!) + edges[0].first.distance(closestEdge.second!!))
                if (newSum < sumWas) {
                    println(listOf(newSum, sumWas))
//                    println(edges)
//                    println(closestEdge)
                    closestEdge.first!!.detachVertex(closestEdge.second!!)
                    edges[0].first.detachVertex(edges[0].second)
                    edges[1].first.detachVertex(edges[1].second)
//                    println(edges[0].first.point.vNum)
//                    println(listOf(edges[0].second, edges[1].second).map { it.point.vNum })
                    edges[0].second.connectToVertex(edges[1].second)

//                    println(listOf(edges[0].first, closestEdge.first!!).map { it.point.vNum })
                    edges[0].first.connectToVertex(closestEdge.first!!)

//                    println(listOf(edges[0].first, closestEdge.second!!).map { it.point.vNum })
                    edges[0].first.connectToVertex(closestEdge.second!!)
                    if (isValid()) {
                        updatesCount++
                        saveEdgesToFile("${vertexes.size}-getRid-$id")
                        break

                    } else {
                        println("hmmmmmmmmmmmm")
                    }
                }
            }
            if (updatesCount == 0) {
                break
            }
        }
    }

    fun rearrangeEdgesPairs() {
        while (true) {
            var updatesCount = 0

            val edgesPairs = vertexes.map { v ->
                v.links.map { link -> Pair(v, link.to) }
            }.flatten().filter { it.first.point.vNum < it.second.point.vNum }.generateUniquePairs()
            for ((edge1, edge2) in edgesPairs) {
                if (listOf(edge1.first, edge1.second, edge2.first, edge2.second).map { it.point.vNum }
                        .distinct().size < 4) continue
                val sumWas = edge1.first.distance(edge1.second) + edge2.first.distance(edge2.second)
                val newSum1 = edge1.first.distance(edge2.first) + edge1.second.distance(edge2.second)
                if (newSum1 < sumWas) {
//                    println(listOf(1, sumWas, newSum1))
//                    println(listOf(edge1.first, edge1.second, edge2.first, edge2.second).map { it.point.vNum })
                    if (!(edge1.first.isLinkedTo(edge2.first) || edge1.second.isLinkedTo(edge2.second))) {
                        edge1.first.connectToVertex(edge2.first)
                        edge1.second.connectToVertex(edge2.second)
                        edge1.first.detachVertex(edge1.second)
                        edge2.first.detachVertex(edge2.second)
                        updatesCount++
                        break
                    }
                }
                val newSum2 = edge1.first.distance(edge2.second) + edge1.second.distance(edge2.first)
                if (newSum2 < sumWas) {

                    if (!(edge1.first.isLinkedTo(edge2.second) || edge1.second.isLinkedTo(edge2.first))) {
                        edge1.first.connectToVertex(edge2.second)
                        edge2.first.connectToVertex(edge1.second)
                        edge1.first.detachVertex(edge1.second)
                        edge2.first.detachVertex(edge2.second)
                        updatesCount++
                        break
                    }
                }
            }
            if (updatesCount == 0) return
            println(weight)
        }
    }

    fun rearrangeEdgesThrees(id: String = "rearrange3") {
        val size = vertexes.size
        var sliceFrom = 0

        while (true) {


            var updatesCount = 0

            val edges = vertexes.map { v ->
                v.links.map { link -> Pair(v, link.to) }
            }.flatten().filter { it.first.point.vNum < it.second.point.vNum }
            var stopFlag = false
            for (i in sliceFrom..size - 3) { //TODO вернуть расчёты с 0
                println(i)
                for (j in i + 1..size - 2) {
                    for (k in j + 1..size - 1) {
                        val edge1 = edges[i]
                        val edge2 = edges[j]
                        val edge3 = edges[k]
                        if (listOf(
                                edge1.first,
                                edge1.second,
                                edge2.first,
                                edge2.second,
                                edge3.first,
                                edge3.second
                            ).map { it.point.vNum }
                                .distinct().size < 6
                        ) continue
                        val sumWas =
                            edge1.first.distance(edge1.second) + edge2.first.distance(edge2.second) + edge3.first.distance(
                                edge3.second
                            )
                        val newSum1 =
                            edge1.first.distance(edge2.first) + edge2.second.distance(edge3.second) + edge1.second.distance(
                                edge3.first
                            )

                        if (newSum1 < sumWas) {
                            if (!(edge1.first.isLinkedTo(edge2.first) || edge1.second.isLinkedTo(edge3.first) || edge2.second.isLinkedTo(
                                    edge3.second
                                ))
                            ) {
                                println(listOf("there1", newSum1, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge2.first)
                                edge1.second.connectToVertex(edge3.first)
                                edge2.second.connectToVertex(edge3.second)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }
                        val newSum2 =
                            edge1.first.distance(edge2.first) + edge2.second.distance(edge3.first) + edge3.second.distance(
                                edge1.second
                            )
                        if (newSum2 < sumWas) {

                            if (!(edge1.first.isLinkedTo(edge2.first) || edge2.second.isLinkedTo(edge3.first) || edge1.second.isLinkedTo(
                                    edge3.second
                                ))
                            ) {
                                println(listOf("there2", newSum2, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge2.first)
                                edge1.second.connectToVertex(edge3.second)
                                edge2.second.connectToVertex(edge3.first)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }

                        val newSum3 =
                            edge1.first.distance(edge2.second) + edge2.first.distance(edge3.second) + edge3.first.distance(
                                edge1.second
                            )
                        if (newSum3 < sumWas) {

                            if (!(edge1.first.isLinkedTo(edge2.second) || edge2.first.isLinkedTo(edge3.second) || edge1.second.isLinkedTo(
                                    edge3.first
                                ))
                            ) {
                                println(listOf("there3", newSum3, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge2.second)
                                edge2.first.connectToVertex(edge3.second)
                                edge3.first.connectToVertex(edge1.second)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }

                        val newSum4 =
                            edge1.first.distance(edge2.second) + edge2.first.distance(edge3.first) + edge3.second.distance(
                                edge1.second
                            )
                        if (newSum4 < sumWas) {
                            if (!(edge1.first.isLinkedTo(edge2.second) || edge2.first.isLinkedTo(edge3.first) || edge1.second.isLinkedTo(
                                    edge3.second
                                ))
                            ) {
                                println(listOf("there4", newSum4, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge2.second)
                                edge2.first.connectToVertex(edge3.first)
                                edge3.second.connectToVertex(edge1.second)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }

                        val newSum5 =
                            edge1.first.distance(edge3.first) + edge3.second.distance(edge2.first) + edge2.second.distance(
                                edge1.second
                            )
                        if (newSum5 < sumWas) {
                            if (!(edge1.first.isLinkedTo(edge3.first) || edge2.first.isLinkedTo(edge3.second) || edge1.second.isLinkedTo(
                                    edge2.second
                                ))
                            ) {
                                println(listOf("there5", newSum5, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge3.first)
                                edge2.second.connectToVertex(edge1.second)
                                edge3.second.connectToVertex(edge2.first)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }

                        val newSum6 =
                            edge1.first.distance(edge3.first) + edge3.second.distance(edge2.second) + edge2.first.distance(
                                edge1.second
                            )
                        if (newSum6 < sumWas) {
                            if (!(edge1.first.isLinkedTo(edge3.first) || edge2.first.isLinkedTo(edge1.second) || edge3.second.isLinkedTo(
                                    edge2.second
                                ))
                            ) {
                                println(listOf("there6", newSum6, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge3.first)
                                edge2.first.connectToVertex(edge1.second)
                                edge3.second.connectToVertex(edge2.second)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }

                        }

                        val newSum7 = edge1.first.distance(edge3.second) + edge3.first.distance(edge2.first) + edge2.second.distance(edge1.second)
                        if (newSum7 < sumWas){
                            if (!(edge1.first.isLinkedTo(edge3.second) || edge2.first.isLinkedTo(edge3.first) || edge1.second.isLinkedTo(
                                    edge2.second
                                ))
                            ) {
                                println(listOf("there7", newSum7, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge3.second)
                                edge2.first.connectToVertex(edge3.first)
                                edge1.second.connectToVertex(edge2.second)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }

                        val newSum8 = edge1.first.distance(edge3.second) + edge3.first.distance(edge2.second) + edge2.first.distance(edge1.second)
                        if (newSum8 < sumWas){
                            if (!(edge1.first.isLinkedTo(edge3.second) || edge2.first.isLinkedTo(edge1.second) || edge3.first.isLinkedTo(
                                    edge2.second
                                ))
                            ) {
                                println(listOf("there8", newSum8, sumWas, edge1, edge2, edge3))

                                edge1.first.connectToVertex(edge3.second)
                                edge3.first.connectToVertex(edge2.second)
                                edge2.first.connectToVertex(edge1.second)
                                edge1.first.detachVertex(edge1.second)
                                edge2.first.detachVertex(edge2.second)
                                edge3.first.detachVertex(edge3.second)
                                updatesCount++
                                stopFlag = true
                                break
                            }
                        }
                    }
                    if (stopFlag) break
                }
                if (stopFlag) {
                    sliceFrom = max(0, i - 10)
                    break
                }
            }
            if (updatesCount == 0) return
            println(weight)
            saveEdgesToFile("$id ${vertexes.size} $weight")

        }
    }

    fun debug() {
        println(vertexes.map { it.degree })
    }

    private fun connect2lastPoint(badVertexes: List<GraphVertex>) {
        var v1saved: GraphVertex? = null
        var v2saved: GraphVertex? = null
        val lastVertex1 = badVertexes[0]
        val lastVertex2 = badVertexes[1]

        val goodVertexes =
            vertexes.filter { it.links.size == 3 && !it.links.any { link -> link.to.point == lastVertex1.point } && !it.links.any { link -> link.to.point == lastVertex2.point } && !(it == lastVertex1 || it == lastVertex1) }
        var best = 100000000

        for (v1 in goodVertexes) {
            for (v2 in goodVertexes) {
                if (!v1.isLinkedTo(v2)) continue
                val coef = lastVertex1.distance(v1) + lastVertex2.distance(v2) - v1.distance(v2)
                if (coef < best) {
                    best = coef
                    v1saved = v1
                    v2saved = v2
                }
            }
        }
        v1saved!!.detachVertex(v2saved!!)
        v1saved.connectToVertex(lastVertex1)
        v2saved.connectToVertex(lastVertex2)
        if (!isValid()) {
//            println(vertexes.map { it.links.size })
            throw Exception("WHAT GOT NOT VALID GRAPH")
        }
    }

    private fun connect1lastPoint(
        badVertexes: List<GraphVertex>,
    ) {

        val lastVertex = badVertexes[0]
        var (v1saved: GraphVertex?, v2saved: GraphVertex?) = getClosestEdgeToVertex(lastVertex)
        v1saved!!.detachVertex(v2saved!!)
        v1saved.connectToVertex(lastVertex)
        v2saved.connectToVertex(lastVertex)
        if (!isValid()) {
            debug()
            throw Exception("WHAT GOT NOT VALID GRAPH")
        }
    }

    private fun getClosestEdgeToVertex(lastVertex: GraphVertex): Pair<GraphVertex?, GraphVertex?> {
        val goodVertexes = vertexes.filter { it.links.size == 3 }
        var best = 100000000
        var v1saved: GraphVertex? = null
        var v2saved: GraphVertex? = null
        for (v1 in goodVertexes) {
            for (v2 in goodVertexes) {
                if (!v1.isLinkedTo(v2) || v1 == v2 || v1 == lastVertex || v2 == lastVertex || v1.links.any { it.to.point == lastVertex.point } || v2.links.any { it.to.point == lastVertex.point }) continue
                val coef = lastVertex.distance(v1) + lastVertex.distance(v2) - v1.distance(v2)
                if (coef < best) {
                    best = coef
                    v1saved = v1
                    v2saved = v2
                }
            }
        }
        return Pair(v1saved, v2saved)
    }


    val weight: Int
        get() = uniqLinks.sumOf { it.weight }
}