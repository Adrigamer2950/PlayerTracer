package me.devadri.playertracer.util

fun <T> MutableList<T>.add(vararg elements: T) {
    this.addAll(elements.toList())
}