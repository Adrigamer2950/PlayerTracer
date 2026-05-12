package me.devadri.playertracer.database.impl

import me.devadri.playertracer.database.LogsDatabase
import org.jetbrains.exposed.v1.jdbc.Database

class SQLiteDatabase : LogsDatabase() {

    override fun initializeDatabase() {
        database = Database.connect("jdbc:sqlite:${plugin.dataFolder.resolve("database").absolutePath}", driver = "org.sqlite.JDBC")
    }
}