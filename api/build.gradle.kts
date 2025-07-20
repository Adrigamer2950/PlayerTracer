plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "${rootProject.group}.api"

val sourcesImplementation = configurations.create("sourcesImplementation")

val unpackSources by tasks.registering(Sync::class) {
    doNotTrackState("Unpack dependency sources")

    from({
        sourcesImplementation.resolve().map { zipTree(it) }
    })
    into(layout.buildDirectory.dir("unpacked-dep-sources"))

    exclude("META-INF/**")
}

tasks.register("sourcesJar", Jar::class) {
    dependsOn(unpackSources)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().allSource)
    from(layout.buildDirectory.dir("unpacked-dep-sources")) {
        exclude("META-INF/**")
    }
    archiveClassifier.set("sources")

    rootProject.subprojects.filter { it.name != "plugin" }.forEach { sub ->
        from(sub.file("src/main/java"))
        from(sub.file("src/main/kotlin"))
    }
}

if (System.getenv("NEXUS_USERNAME") != null) {
    publishing {
        repositories {
            maven {
                val baseUrl = "https://repo.devadri.es/repository/"

                url = uri(
                    baseUrl + if ((version as String).lowercase().contains("beta")) "dev" else "releases"
                )
                credentials {
                    username = System.getenv("NEXUS_USERNAME") ?: throw IllegalArgumentException("NEXUS_USERNAME env variable is not set")
                    password = System.getenv("NEXUS_PASSWORD") ?: throw IllegalArgumentException("NEXUS_PASSWORD env variable is not set")
                }
            }
        }
        publications {
            create<MavenPublication>("artifact") {
                groupId = group as String
                artifactId = rootProject.name
                version = rootProject.version as String

                from(components["kotlin"])
                artifact(tasks["sourcesJar"])
                pom {
                    name = rootProject.name
                    description.set(parent?.properties?.get("description") as String)
                    url = "https://github.com/Adrigamer2950/PlayerTracer"

                    licenses {
                        license {
                            name = "GPL-3.0"
                            url = "https://www.gnu.org/licenses/gpl-3.0.html"
                        }
                    }

                    developers {
                        developer {
                            id = "Adrigamer2950"
                            name = "Adri"
                        }
                    }

                    scm {
                        url = "https://github.com/Adrigamer2950/PlayerTracer"
                    }

                    issueManagement {
                        system = "GitHub"
                        url = "https://github.com/Adrigamer2950/PlayerTracer/issues"
                    }
                }
            }
        }
    }
}