package org.example.classes

import Point

class GraphAllVertexes(val points: List<Point>) : Graph() {

    init {
        points.forEach { vertexes.add(GraphVertex(it)) }
    }

    fun calcCliques4(toLeft: Int) {
        var vertexesTmp = vertexes.filter { it.degree ==0 }.map { it.cloneWithoutLinks() }
        while (vertexesTmp.size > toLeft) {
            println(vertexesTmp.size)
            var minCliqueWeight = 10000000
            var saveV1: GraphVertex? = null
            var saveV2: GraphVertex? = null
            var saveV3: GraphVertex? = null
            var saveV4: GraphVertex? = null
            for (index1 in vertexesTmp.indices - 3) {
                println("---$index1")
                for (index2 in index1 + 1..<vertexesTmp.size - 2)
                    for (index3 in index2 + 1..<vertexesTmp.size - 1)
                        for (index4 in index3 + 1..<vertexesTmp.size) {
                            val v1 = vertexesTmp[index1]
                            val v2 = vertexesTmp[index2]
                            val v3 = vertexesTmp[index3]
                            val v4 = vertexesTmp[index4]
                            val cliqueWeight =
                                v1.distance(v2) + v1.distance(v3) + v1.distance(v4) + v2.distance(v3) + v2.distance(v4) + v3.distance(
                                    v4
                                )
                            if (cliqueWeight < minCliqueWeight) {
                                minCliqueWeight = cliqueWeight
                                saveV1 = v1
                                saveV2 = v2
                                saveV3 = v3
                                saveV4 = v4
                            }
                        }
            }
            if (saveV4 != null) {
                val realV1 = vertexes.find { it == saveV1 }!!
                val realV2 = vertexes.find { it == saveV2 }!!
                val realV3 = vertexes.find { it == saveV3 }!!
                val realV4 = vertexes.find { it == saveV4 }!!
                realV1.connectToVertex(realV2)
                realV1.connectToVertex(realV3)
                realV1.connectToVertex(realV4)
                realV2.connectToVertex(realV3)
                realV2.connectToVertex(realV4)
                realV3.connectToVertex(realV4)
            }
            vertexesTmp = vertexes.filter { it.degree ==0 }.map { it.cloneWithoutLinks() }
        }

    }

    fun calcCliques3(toLeft: Int) {
        var vertexesTmp = vertexes.filter { it.degree ==0 }.map { it.cloneWithoutLinks() }
        while (vertexesTmp.size > toLeft) {
            println(vertexesTmp.size)
            var minCliqueWeight = 10000000
            var saveV1: GraphVertex? = null
            var saveV2: GraphVertex? = null
            var saveV3: GraphVertex? = null
            for (index1 in vertexesTmp.indices - 2) {
                println("---$index1")
                for (index2 in index1 + 1..<vertexesTmp.size - 1)
                    for (index3 in index2 + 1..<vertexesTmp.size) {
                        val v1 = vertexesTmp[index1]
                        val v2 = vertexesTmp[index2]
                        val v3 = vertexesTmp[index3]
                        val cliqueWeight =
                            v1.distance(v2) + v1.distance(v3) + v2.distance(v3)
                        if (cliqueWeight < minCliqueWeight) {
                            minCliqueWeight = cliqueWeight
                            saveV1 = v1
                            saveV2 = v2
                            saveV3 = v3
                        }
                    }
            }
            if (saveV3 != null) {
                val realV1 = vertexes.find { it == saveV1 }!!
                val realV2 = vertexes.find { it == saveV2 }!!
                val realV3 = vertexes.find { it == saveV3 }!!
                realV1.connectToVertex(realV2)
                realV1.connectToVertex(realV3)
                realV2.connectToVertex(realV3)
            }
            vertexesTmp = vertexes.filter { it.degree ==0 }.map { it.cloneWithoutLinks() }
        }
    }
}