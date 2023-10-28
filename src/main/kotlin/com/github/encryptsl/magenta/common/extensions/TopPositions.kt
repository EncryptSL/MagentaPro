package com.github.encryptsl.magenta.common.extensions

inline fun <T, R> Iterable<T>.positionIndexed(transform: (index: Int, T) -> R): List<R> {
    return mapIndexedTo(ArrayList(collectionSizeOrDefault(10)), transform)
}

inline fun <T> Iterable<T>.forEachIndexed(action: (index: Int, T) -> Unit): Unit {
    var index = 1
    for (item in this) action(index++, item)
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapIndexedTo(destination: C, transform: (index: Int, T) -> R): C {
    var index = 1
    for (item in this)
        destination.add(transform(index++, item))
    return destination
}

fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int = if (this is Collection<*>) this.size else default