package org.example.utils

import Point
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.classes.GraphAllVertexes
import java.util.concurrent.Executors

fun parallelRunnerShuffler(pointsArg: List<Point>) {
    var iterCount = 0
    val executor = Executors.newFixedThreadPool(16).asCoroutineDispatcher()
    var best = 250000000


    runBlocking {
        repeat(16) { threadNumber ->
            launch(executor) {
//                var bestLocal = 250000000

                while (true) {
//                    val start = pointsArg.random()
//                    val start = pointsArg[201]

//                    var points = pointsArg.filter { it!=start }
//                    val shuffledPoints = points.shuffledWithinLimits((best - 380000) / 100 + 2)
//                    println(iterCount)
                    val graph = GraphAllVertexes(pointsArg)
                    graph.searchForBestGraphBasedOnRandomFixDegrees("${pointsArg.size}-${threadNumber}")
//                    val graph = GraphIncremental(start)
//                    val shuffledPoints = points.shuffled()
//                    val shuffledPoints = points.shuffledWithinLimits((best - 17600) / 75 + 2)
//                    val shuffledPoints = points.shuffledWithinLimits(if (best > 2400) 64 else 10)
//                    val graph = GraphIncremental(shuffledPoints[0])
//                    try {
//                        graph.dumbFillBy1(points, sortByStart = true, shuffleLimit=1)
//                        graph.dumbFillBy1Or2(points, true, withFix = false)
//                        graph.randomFixDegrees(limit=12)
//
//                    } catch (e: Exception) {
//                        println("error")
//                        continue
//                    }
//                    if (iterCount % 100_000 == 0) {
//                        println(iterCount)
//                        println("bestLocal $bestLocal")
//                        println("limit: ${(bestLocal - 800) / 10}")
//                    }
//                    if (w < bestLocal) {
//                        bestLocal = w
//                    }

//                    println(w)
//                    if (w>145_000){
//                        println("best")
//                        graph.saveEdgesToFile("filename2")
//                    }
                }

            }
        }
    }
    executor.close()
}