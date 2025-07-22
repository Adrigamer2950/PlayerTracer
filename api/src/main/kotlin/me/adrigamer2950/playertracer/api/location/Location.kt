package me.adrigamer2950.playertracer.api.location

import kotlin.math.floor
import kotlin.math.pow

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

        @JvmStatic
        private fun truncateDouble(double: Double): Double { // 2 decimals
            val factor = 10.0.pow(2)
            return floor(double * factor) / factor
        }
    }
}