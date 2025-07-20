package me.adrigamer2950.playertracer.database.impl

import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.database.LogsDatabase
import org.jetbrains.exposed.sql.Database
import java.io.File

class H2Database(plugin: PlayerTracerPlugin) : LogsDatabase(plugin) {

    override fun connect() {
        // Initialize database field
        database = Database.connect("jdbc:h2:file:${File(plugin.dataFolder, "database").absolutePath}", driver = "org.h2.Driver")

        super.connect()
    }
}