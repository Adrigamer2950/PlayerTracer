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

        Database.Remote.hostname = yaml.getString("database.remote.hostname")
        Database.Remote.port = yaml.getString("database.remote.port")
        Database.Remote.database = yaml.getString("database.remote.database")
        Database.Remote.username = yaml.getString("database.remote.username")
        Database.Remote.password = yaml.getString("database.remote.password")
    }

    object Database {

        lateinit var driver: Driver
            internal set

        enum class Driver {
            H2,
            SQLITE,
            MYSQL,
            MARIADB,
            POSTGRESQL
        }

        object Remote {

            lateinit var hostname: String
                internal set

            lateinit var port: String
                internal set

            lateinit var database: String
                internal set

            lateinit var username: String
                internal set

            lateinit var password: String
                internal set
        }
    }
}