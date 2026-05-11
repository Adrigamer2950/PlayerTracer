package me.devadri.playertracer.api.logs

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMetadata(
    val id: String,
    val description: String,
    val displayName: String = ""
)