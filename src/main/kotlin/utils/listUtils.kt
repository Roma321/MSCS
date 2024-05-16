package org.example.utils

fun <T> List<T>.shuffledWithinLimits(moveLimit: Int): List<T> {
    val shuffledList = this.toMutableList()
    for (i in shuffledList.indices) {
        val randomIndex = (i..Math.min(i + moveLimit, shuffledList.lastIndex)).random()
        shuffledList[i] = shuffledList[randomIndex].also { shuffledList[randomIndex] = shuffledList[i] }
    }
    return shuffledList
}

fun <T> List<T>.generateUniquePairs(): List<Pair<T, T>> {
    val uniquePairs = mutableListOf<Pair<T, T>>()

    for (i in indices) {
        for (j in i + 1 until size) {
            uniquePairs.add(Pair(get(i), get(j)))
        }
    }

    return uniquePairs
}
