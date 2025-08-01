package me.adrigamer2950.playertracer.database.impl.remote

import me.adrigamer2950.playertracer.Config
import me.adrigamer2950.playertracer.database.LogsDatabase
import org.jetbrains.exposed.sql.Database

abstract class RemoteDatabase(protected val type: String, protected val driver: String) : LogsDatabase() {

    override fun initializeDatabase() {
        val host = Config.Database.Remote.hostname
        val port = Config.Database.Remote.port
        val db = Config.Database.Remote.database

        database = Database.connect(
            "jdbc:$type://$host:$port/$db",
            driver,
            user = Config.Database.Remote.username,
            password = Config.Database.Remote.password,
        )
    }
}