package me.adrigamer2950.playerlogs.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun launchCoroutine(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(context).launch {
        block()
    }
}