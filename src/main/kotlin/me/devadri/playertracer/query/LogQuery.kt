package me.devadri.playertracer.query

import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.database.DatabaseFailureLog
import java.sql.Timestamp
import java.util.UUID

class LogQuery(
    val uuids: Array<UUID>,
    var actions: MutableList<Class<out Log>>,
    val after: Timestamp? = null,
    val afterS: String? = null
) {

    suspend fun getResults(): List<Log> {
        return PlayerTracerPlugin.instance.database.getLogs(*uuids).filter { log ->
            actions.contains(log::class.java) || log is DatabaseFailureLog
        }.filter { after == null || Timestamp(it.timestamp).after(after) }
    }
}