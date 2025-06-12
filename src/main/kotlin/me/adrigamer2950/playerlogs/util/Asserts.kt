package me.adrigamer2950.playerlogs.util

fun assertTrue(condition: Boolean, message: String): Boolean {
    return assertTrue(condition) {
        throw AssertionError(message)
    }
}

@JvmOverloads
fun assertTrue(condition: Boolean, action: () -> Unit = {
    throw AssertionError("Assertion failed")
}): Boolean {
    if (condition) return true

    action.invoke()
    return false
}