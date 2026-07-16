package me.devadri.playertracer.api.logs.filter

import org.bukkit.entity.Player

class DefaultLogQueryFilter : LogQueryFilter {

    override fun isAllowedToQuery(player: Player): Boolean = true
}