package me.adrigamer2950.playerlogs.util

import me.adrigamer2950.adriapi.api.logger.Logger
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
object Asserts {

    private lateinit var logger: Logger

    fun setLogger(logger: Logger) {
        this.logger = logger
    }

    /**
     * @return True if condition is false
     */
    fun assert(comment: String, condition: () -> Boolean): Boolean {
        if (condition.invoke()) return false

        logger.error("Assertion failed", AssertionException(comment))

        return true
    }
}

class AssertionException(message: String) : RuntimeException(message)