package me.adrigamer2950.playertracer.util

fun <T> MutableList<T>.add(vararg elements: T) {
    this.addAll(elements.toList())
}