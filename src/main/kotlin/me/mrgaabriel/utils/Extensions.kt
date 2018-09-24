package me.mrgaabriel.utils

import java.util.*

fun List<String>.random(): String {
    val random = SplittableRandom()

    return this[random.nextInt(this.size - 1)]
}
