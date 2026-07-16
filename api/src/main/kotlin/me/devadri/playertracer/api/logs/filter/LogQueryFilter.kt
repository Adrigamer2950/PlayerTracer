package me.devadri.playertracer.api.logs.filter

import org.bukkit.entity.Player

interface LogQueryFilter {

    /**
     * Determines whether the given player has permission to query this log.
     *
     * For example, if there's a log that tracks player's IP address changes,
     * this method could restrict low-level staff from querying it, only allowing
     * high-staff members to do so.
     *
     * @param player The player attempting to query this log
     * @return `true` if the player is allowed to query this log, `false` otherwise
     */
    fun isAllowedToQuery(player: Player): Boolean
}