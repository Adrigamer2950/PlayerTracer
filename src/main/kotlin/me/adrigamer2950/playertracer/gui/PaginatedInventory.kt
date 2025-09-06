package me.adrigamer2950.playertracer.gui

import me.devadri.obsidian.inventory.Inventory
import me.devadri.obsidian.inventory.InventorySize
import me.devadri.obsidian.item.ItemBuilder
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import net.kyori.adventure.text.Component

abstract class PaginatedInventory(title: Component, size: InventorySize) : Inventory(
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