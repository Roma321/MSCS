package utils

import Point
import java.io.File
import java.io.IOException

fun readFromFile(fileName: String): List<Point> {
    val points = mutableListOf<Point>()
    val file = File(fileName)
    if (!file.exists()) {
        println("File not found.")
        throw IOException()
    }

    val lines = file.readLines() // Read all lines from the file

    for ((idx, line) in lines.withIndex()) {
        if (idx == 0) continue
        points.add(Point(line, idx))
    }


    return points
}