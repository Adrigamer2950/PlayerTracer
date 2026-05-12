package me.devadri.playertracer.database.impl

import me.devadri.playertracer.database.LogsDatabase
import org.jetbrains.exposed.v1.jdbc.Database
import java.io.File

class H2Database : LogsDatabase() {

    override fun initializeDatabase() {
        database = Database.connect("jdbc:h2:file:${File(plugin.dataFolder, "database").absolutePath}", driver = "org.h2.Driver")
    }
}