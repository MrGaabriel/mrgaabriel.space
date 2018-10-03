package me.mrgaabriel.utils.extensions

import java.util.*

fun <T> List<T>.random(): T {
    return this[SplittableRandom().nextInt(this.size)]
}