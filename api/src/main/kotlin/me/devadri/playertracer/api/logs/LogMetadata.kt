package me.devadri.playertracer.api.logs

/**
 * Annotation to store basic metadata for [Log] implementations.
 *
 * It must always be included in any [Log] implementation,
 * as it won't be able to be registered otherwise
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMetadata(
    val id: String,
    val description: String,
    val displayName: String = ""
)