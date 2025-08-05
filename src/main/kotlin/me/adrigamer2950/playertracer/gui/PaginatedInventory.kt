package me.adrigamer2950.playertracer.gui

import me.adrigamer2950.adriapi.api.inventory.Inventory
import me.adrigamer2950.adriapi.api.inventory.InventorySize
import me.adrigamer2950.adriapi.api.item.ItemBuilder
import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import net.kyori.adventure.text.Component

abstract class PaginatedInventory(user: User, title: Component, size: InventorySize) : Inventory(
    user,
    title,
    PlayerTracerPlugin.instance,
    size
) {

    private var _currentPage: Int = 0

    var currentPage: Int
        get() = _currentPage
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Number cannot be negative")
            }

            _currentPage = value
        }

    abstract val listToIterate: List<Pair<ItemBuilder, Int>>
}