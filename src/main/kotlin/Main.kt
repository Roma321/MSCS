import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.classes.GraphAllVertexes
import org.example.utils.parallelRunnerShuffler
import utils.readFromFile

//class Main {
//
//    fun main(args: Array<String>){
//        println("hello world")
//    }
//}

fun main(args: Array<String>){
    val points = readFromFile("Benchmark/Taxicab_4096.txt")

//    val pointsLeftTop = points.filter { it.x <= 2046 && it.y<=2047 }//100223->99649
//    val pointsLeftBottom = points.filter { it.x <=2046 && it.y>2047 }//93806->91656
//    val pointsRightTop = points.filter { it.x > 2046 && it.y<2048 }//94602->92106
//    val pointsRightBottom = points.filter { it.x > 2046 && it.y>=2048 }//95626->95540
////    println(pointsRightBottom.size)
//    runBlocking {
//        val jobs = List(16) { index ->
//            launch {
//                val graph = GraphAllVertexes(points)
//                graph.loadEdgesFromFile("output/${points.size}-${index}.txt")
//                graph.tryGetRidOfLongestEdges(index.toString())
//                graph.saveEdgesToFile("${points.size}-${graph.weight}")
//                println(graph.weight)
//            }
//        }
//        jobs.joinAll()
//    }
////
    val graph = GraphAllVertexes(points)
//    graph.loadEdgesFromFile("output/${points.size}-${379649}.txt")
//    graph.tryGetRidOfLongestEdges()
//    graph.saveEdgesToFile("${points.size}-$${graph.weight}")
//    println(graph.weight)
//    parallelRunnerShuffler(points)
    graph.loadEdgesFromFile("/home/roman/projects/my/MSCS/output/4096-350223.txt")

    println(graph.weight)
//    graph.rearrangeEdgesThrees()
    graph.rearrangeEdgesPairs()
    graph.saveEdgesToFile("${points.size}-$${graph.weight}")
//    graph.tryGetRidOfLongestEdges()
//    println(graph.weight)
//    graph.tryGetRidOfLongestEdges()
//    println(graph.weight)
//    graph.tryGetRidOfLongestEdges()
//    println(graph.weight)
//    graph.tryGetRidOfLongestEdges()
//    println(graph.weight)
//    graph.tryGetRidOfLongestEdges()
//    println(graph.weight)
//    graph.tryGetRidOfLongestEdges()
//    println(graph.weight)
//    graph.tryGetRidOfLongestEdges()

}