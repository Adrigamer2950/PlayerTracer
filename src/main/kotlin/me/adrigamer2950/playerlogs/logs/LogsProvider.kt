package me.adrigamer2950.playerlogs.logs

import me.adrigamer2950.adriapi.api.logger.Logger
import kotlin.reflect.KClass

class LogsProvider(private val logger: Logger) {

    private val logs: MutableSet<KClass<out Log>> = mutableSetOf()

    fun registerLog(vararg classes: KClass<out Log>) {
        classes.forEach {
            if (isLogRegistered(it)) return@forEach

            logs.add(it)

            logger.debug("Registered log ${it.qualifiedName}")
        }
    }

    fun isLogRegistered(clazz: KClass<out Log>): Boolean = logs.contains(clazz)
}