@file:Suppress("VulnerableLibrariesLocal")

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer
import xyz.jpenilla.runtask.task.AbstractRun

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

group = "me.adrigamer2950.playertracer"
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

        implementation(rootProject.libs.adriapi)

        implementation(kotlin("stdlib-jdk8"))
    }
}

dependencies {
    implementation(project(":api"))

    compileOnly(libs.exposed.core)
    compileOnly(libs.exposed.dao)
    compileOnly(libs.exposed.jdbc)
    compileOnly(libs.h2)
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
            description = "Allows to teleport to players"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

tasks.build {
    finalizedBy("shadowJar")
}

tasks.shadowJar {
    archiveClassifier.set("")
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
    minecraftVersion("1.20.6")

    downloadPlugins {
        // ViaVersion
        hangar("ViaVersion", "5.2.0")

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
        val fileSpec = FileSpec.builder("me.adrigamer2950.playertracer", "BuildConstants")
            .addType(
                TypeSpec.objectBuilder("BuildConstants")
                    .addProperty(
                        PropertySpec.builder("VERSION", String::class)
                            .initializer("%S", rootProject.version as String)
                            .addModifiers(KModifier.CONST)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("H2_VERSION", String::class)
                            .initializer("%S", libs.versions.h2.get())
                            .addModifiers(KModifier.CONST)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("KOTLIN_VERSION", String::class)
                            .initializer("%S", libs.versions.kotlin.get())
                            .addModifiers(KModifier.CONST)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("EXPOSED_VERSION", String::class)
                            .initializer("%S", libs.versions.exposed.get())
                            .addModifiers(KModifier.CONST)
                            .build()
                    )
                    .build()
            )
            .build()

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
