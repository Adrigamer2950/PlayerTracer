package me.adrigamer2950.playertracer.database.tables

import org.jetbrains.exposed.sql.Table

object LogsTable : Table("logs") {

    val id = ulong("id").autoIncrement()
    override val primaryKey = PrimaryKey(id, name = "log_id")
    val playerUUID = varchar("playerUUID", 36)
    val `class` = varchar("class", 256)
    val data = text("data")
}