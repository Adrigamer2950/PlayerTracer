package me.devadri.playertracer.viewmode

import dev.dejvokep.boostedyaml.YamlDocument
import me.devadri.playertracer.PlayerTracerPlugin
import java.io.File
import java.util.UUID

object ViewModeManager {

    @JvmField
    val DEFAULT = ViewMode.CHAT

    private val players = mutableMapOf<UUID, ViewMode>()

    private lateinit var yaml: YamlDocument

    @JvmStatic
    fun init() {
        yaml = YamlDocument.create(
            File(PlayerTracerPlugin.instance.dataFolder, "viewmodes.yml"),
            PlayerTracerPlugin.instance::class.java.classLoader.getResourceAsStream("viewmodes.yml") ?: throw IllegalStateException("viewmodes.yml not found in plugin resources")
        )

        yaml.reload()

        yaml.getSection("players")?.let { section ->
            section.keys.forEach { key ->
                val uuid = runCatching { UUID.fromString(key.toString()) }.getOrNull() ?: return@forEach
                val mode = ViewMode.entries.find { it.name.equals(section.getString(key.toString()), true) } ?: return@forEach

                players[uuid] = mode
            }
        }
    }

    @JvmStatic
    fun save() {
        yaml.remove("players")

        for ((uuid, mode) in players) {
            yaml.set("players.$uuid", mode.name)
        }

        yaml.save()
    }

    fun get(uuid: UUID?): ViewMode {
        if (uuid == null) return DEFAULT

        return players.getOrDefault(uuid, DEFAULT)
    }

    fun set(uuid: UUID, viewMode: ViewMode) {
        players[uuid] = viewMode
    }
}