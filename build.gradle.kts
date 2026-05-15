@file:Suppress("VulnerableLibrariesLocal")

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer
import xyz.jpenilla.runtask.task.AbstractRun
import kotlin.reflect.KClass

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.run.server)
    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar.publish)
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlinpoet)
    }
}

group = "me.devadri.playertracer"
version = properties["version"] as String

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    val targetJavaVersion = (properties["java-version"] as String).toInt()

    kotlin {
        jvmToolchain(targetJavaVersion)
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
        maven("https://repo.devadri.es/repository/releases") {
            name = "devadri"
        }
    }

    dependencies {
        compileOnly(rootProject.libs.jetbrains.annotations)

        compileOnly(rootProject.libs.paper.api)

        implementation(rootProject.libs.obsidian)

        implementation(kotlin("stdlib-jdk8"))
    }
}

dependencies {
    implementation(project(":api"))

    compileOnly(libs.exposed.core)
    compileOnly(libs.exposed.dao)
    compileOnly(libs.exposed.jdbc)

    compileOnly(libs.h2)
    compileOnly(libs.sqlite)
    compileOnly(libs.mysql)
    compileOnly(libs.mariadb)
    compileOnly(libs.postgresql)

    compileOnly(libs.boosted.yaml)
}

bukkit {
    name = rootProject.name
    main = properties["main-class"] as String
    apiVersion = properties["api-version"] as String
    author = properties["author"] as String
    website = properties["website"] as String
    description = properties["description"] as String
    foliaSupported = true

    permissions {
        register("playertracer.admin") {
            description = "All permissions"
            default = BukkitPluginDescription.Permission.Default.OP

            children = listOf(
                "playertracer.search",
                "playertracer.teleport"
            )
        }

        register("playertracer.search") {
            description = "Allows to search logs"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("playertracer.teleport") {
            description = "Allows to use '/pt tp'"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

tasks.shadowJar {
    archiveClassifier.set("")

    dependencies {
        relocate("me.devadri.obsidian", "me.devadri.playertracer.libs.obsidian")
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = properties["modrinth-id"] as String
    versionNumber = version as String
    versionName = rootProject.name + " " + version
    versionType = properties["modrinth-type"] as String
    uploadFile.set(tasks.shadowJar.get().archiveFile)

    gameVersions.set(
        (properties["modrinth-version"] as String)
            .split(",")
            .map { it.trim() }
    )

    val modrinthLoaders: List<String> = (properties["modrinth-loaders"] as String)
        .split(",")
        .map { it.trim() }
    loaders.set(modrinthLoaders)

    syncBodyFrom = rootProject.file("README_MODRINTH.md").readText()
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set(properties["hangar-channel"] as String)
        id.set(properties["hangar-id"] as String)
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.get().archiveFile)

                val versions: List<String> = (properties["hangar-version"] as String)
                    .split(",")
                    .map { it.trim() }
                platformVersions.set(versions)
            }
        }
    }
}

tasks.named<RunServer>("runServer").configure {
    minecraftVersion("1.21.11")

    downloadPlugins {
        // ViaVersion
        hangar("ViaVersion", "5.9.1")

        // ViaBackwards
        hangar("ViaBackwards", "5.9.1")

        modrinth("luckperms", "v5.5.0-bukkit")
    }
}

tasks.withType(AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        @Suppress("UnstableApiUsage")
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs(
        // Hot Swap
        "-XX:+AllowEnhancedClassRedefinition",

        // Aikar Flags
        "--add-modules=jdk.incubator.vector", "-XX:+UseG1GC", "-XX:+ParallelRefProcEnabled",
        "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions", "-XX:+DisableExplicitGC",
        "-XX:+AlwaysPreTouch", "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40",
        "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20", "-XX:G1HeapWastePercent=5",
        "-XX:G1MixedGCCountTarget=4", "-XX:InitiatingHeapOccupancyPercent=15",
        "-XX:G1MixedGCLiveThresholdPercent=90", "-XX:G1RSetUpdatingPauseTimePercent=5",
        "-XX:SurvivorRatio=32", "-XX:+PerfDisableSharedMem", "-XX:MaxTenuringThreshold=1",
        "-Dusing.aikars.flags=https://mcflags.emc.gs", "-Daikars.new.flags=true"
    )

}

tasks.register("generateBuildConstants") {
    doLast {
        val fileSpecBuilder = FileSpec.builder("me.devadri.playertracer", "BuildConstants")
        val objectBuilder = TypeSpec.objectBuilder("BuildConstants")

        listOf<Triple<String, KClass<out Any>, Any>>(
            Triple("VERSION",   String::class, rootProject.version as String),
            Triple("H2_VERSION", String::class, libs.versions.h2.get()),
            Triple("SQLITE_VERSION", String::class, libs.versions.sqlite.get()),
            Triple("MYSQL_VERSION", String::class, libs.versions.mysql.get()),
            Triple("MARIADB_VERSION", String::class, libs.versions.mariadb.get()),
            Triple("POSTGRESQL_VERSION", String::class, libs.versions.postgresql.get()),
            Triple("KOTLIN_VERSION", String::class, libs.versions.kotlin.get()),
            Triple("EXPOSED_VERSION", String::class, libs.versions.exposed.get()),
            Triple("BOOSTED_YAML_VERSION", String::class, libs.versions.boosted.yaml.get()),
        ).forEach {
            objectBuilder.addProperty(
                PropertySpec.builder(it.first, it.second)
                    .initializer("%S", it.third)
                    .addModifiers(KModifier.CONST)
                    .build()
            )
        }
        
        val fileSpec = fileSpecBuilder.addType(objectBuilder.build()).build()

        val generatedDir = layout.buildDirectory.dir("generated/templates").get().asFile
        fileSpec.writeTo(generatedDir)
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("generateBuildConstants"))
}

val generatedDir = layout.buildDirectory.dir("generated/templates").get().asFile

sourceSets.main {
    java.srcDirs("src/main/java")
    kotlin.srcDirs("src/main/kotlin", generatedDir)
}
