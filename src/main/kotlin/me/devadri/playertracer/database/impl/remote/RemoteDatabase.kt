package me.devadri.playertracer.database.impl.remote

import me.devadri.playertracer.Config
import me.devadri.playertracer.database.LogsDatabase
import org.jetbrains.exposed.v1.jdbc.Database

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