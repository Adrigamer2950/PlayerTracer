package me.adrigamer2950.playertracer.database.impl

import me.adrigamer2950.playertracer.database.LogsDatabase
import org.jetbrains.exposed.sql.Database

class SQLiteDatabase : LogsDatabase() {

    override fun initializeDatabase() {
        database = Database.connect("jdbc:sqlite:${plugin.dataFolder.resolve("database").absolutePath}", driver = "org.sqlite.JDBC")
    }
}