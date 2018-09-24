package me.mrgaabriel.utils

import java.util.*

fun List<String>.random(): String {
    val random = SplittableRandom()

    return this[random.nextInt(this.size - 1)]
}

fun List<String>.randomOrNull(): String? {
    val random = SplittableRandom()

    return this.getOrNull(random.nextInt(this.size - 1))
}

