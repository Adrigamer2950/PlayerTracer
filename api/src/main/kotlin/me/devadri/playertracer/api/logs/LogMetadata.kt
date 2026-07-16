package me.devadri.playertracer.api.logs

import me.devadri.playertracer.api.logs.filter.DefaultLogQueryFilter
import me.devadri.playertracer.api.logs.filter.LogQueryFilter
import kotlin.reflect.KClass

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
    val displayName: String = "",
    val queryFilter: KClass<out LogQueryFilter> = DefaultLogQueryFilter::class
)