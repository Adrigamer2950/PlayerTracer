package me.devadri.playertracer

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import java.io.File
import kotlin.properties.Delegates

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
            it.name == yaml.getString("database.driver", "H2").uppercase()
        } ?: throw IllegalArgumentException("Invalid database driver specified in config.yml: ${yaml.getString("database.driver")}")

        Database.Remote.hostname = yaml.getString("database.remote.hostname", "localhost")
        Database.Remote.port = yaml.getString("database.remote.port", "3306")
        Database.Remote.database = yaml.getString("database.remote.database", "playertracer")
        Database.Remote.username = yaml.getString("database.remote.username", "root")
        Database.Remote.password = yaml.getString("database.remote.password", "")

        Database.threadLimit = yaml.getInt("misc.thread-limit", 4)

        if (Database.threadLimit < 1) {
            PlayerTracerPlugin.instance.logger.warn("Thread limit cannot be lower than 1. Reverting to default (4)")
            Database.threadLimit = 4
        }

        Logs.JOIN = yaml.getBoolean("logs.join", true)
        Logs.LEAVE = yaml.getBoolean("logs.leave", true)
        Logs.CHAT = yaml.getBoolean("logs.chat", true)
        Logs.COMMAND = yaml.getBoolean("logs.command", true)
    }

    object Database {

        lateinit var driver: Driver

        enum class Driver {
            H2,
            SQLITE,
            MYSQL,
            MARIADB,
            POSTGRESQL
        }

        object Remote {

            lateinit var hostname: String

            lateinit var port: String

            lateinit var database: String

            lateinit var username: String

            lateinit var password: String
        }

        var threadLimit by Delegates.notNull<Int>()
    }

    object Logs {

        var JOIN by Delegates.notNull<Boolean>()

        var LEAVE by Delegates.notNull<Boolean>()

        var CHAT by Delegates.notNull<Boolean>()

        var COMMAND by Delegates.notNull<Boolean>()
    }
}