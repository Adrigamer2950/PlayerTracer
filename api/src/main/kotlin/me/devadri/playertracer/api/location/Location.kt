package me.devadri.playertracer.api.location

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor
import kotlin.math.pow

/**
 * Simple class to store logs' location. Including the world's name and XYZ coordinates
 */
class Location(val worldName: String, x: Double, y: Double, z: Double) {

    val x: Double = truncateDouble(x)
    val y: Double = truncateDouble(y)
    val z: Double = truncateDouble(z)

    companion object {
        @JvmStatic
        fun fromBukkitLocation(location: org.bukkit.Location): Location {
            return Location(
                location.world?.name ?: "world",
                location.x,
                location.y,
                location.z
            )
        }

        /**
         * Truncate doubles to only include 2 decimals
         */
        @JvmStatic
        private fun truncateDouble(double: Double): Double {
            val factor = 10.0.pow(2)
            return floor(double * factor) / factor
        }

        @JvmField
        val PERSISTENT_DATA_TYPE = object : PersistentDataType<String, Location> {
            override fun getPrimitiveType(): Class<String> = String::class.java

            override fun getComplexType(): Class<Location> = Location::class.java

            override fun toPrimitive(
                complex: Location,
                context: PersistentDataAdapterContext
            ): String = "${complex.worldName}:${complex.x}:${complex.y}:${complex.z}"

            override fun fromPrimitive(
                primitive: String,
                context: PersistentDataAdapterContext
            ): Location {
                val l = primitive.split(":")

                // XYZ coordinates fallback to 0.0 in case they aren't specified
                val worldName = l[0]
                val x = l[1].toDoubleOrNull() ?: 0.0
                val y = l[2].toDoubleOrNull() ?: 0.0
                val z = l[3].toDoubleOrNull() ?: 0.0

                return Location(worldName, x, y, z)
            }
        }
    }
}