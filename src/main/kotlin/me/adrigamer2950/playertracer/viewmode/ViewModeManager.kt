package me.adrigamer2950.playertracer.viewmode

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import me.adrigamer2950.playertracer.PlayerTracerPlugin
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
            PlayerTracerPlugin.instance::class.java.getResourceAsStream("viewmodes.yml") ?: throw IllegalStateException("viewmodes.yml not found in plugin resources"),
            LoaderSettings.builder().setAutoUpdate(true).build()
        )

        yaml.reload()

        yaml.getSection("players")?.let {
            for (entry in it.keys) {
                val uuid = UUID.fromString(entry as String)
                val modeName = it.getString(entry) ?: continue
                val mode = ViewMode.entries.find { it.name.equals(modeName, true) } ?: continue
                players[uuid] = mode
            }
        } ?: run {
            yaml.set("players", emptyMap<String, String>())
        }
    }

    @JvmStatic
    fun save() {
        yaml.set("players", players.mapValues { it.value.name })
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