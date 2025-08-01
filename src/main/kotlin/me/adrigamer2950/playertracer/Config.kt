package me.adrigamer2950.playertracer

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import java.io.File

object Config {

    private lateinit var yaml: YamlDocument

    @JvmStatic
    fun init() {
        yaml = YamlDocument.create(
            File(PlayerTracerPlugin.instance.dataFolder, "config.yml"),
            PlayerTracerPlugin::class.java.classLoader.getResourceAsStream("config.yml") ?: throw IllegalStateException("config.yml not found"),
            GeneralSettings.DEFAULT,
            LoaderSettings.builder().setAutoUpdate(true).build(),
            DumperSettings.DEFAULT,
            UpdaterSettings.DEFAULT
        )

        yaml.reload()

        Database.driver = Database.Driver.entries.firstOrNull {
            it.name == yaml.getString("database.driver").uppercase()
        } ?: throw IllegalArgumentException("Invalid database driver specified in config.yml: ${yaml.getString("database.driver")}")
    }

    object Database {

        lateinit var driver: Driver
            internal set

        enum class Driver {
            H2,
            SQLITE
        }
    }
}