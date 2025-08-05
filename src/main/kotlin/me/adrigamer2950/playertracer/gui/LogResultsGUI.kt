package me.adrigamer2950.playertracer.gui

import me.adrigamer2950.adriapi.api.inventory.InventorySize
import me.adrigamer2950.adriapi.api.item.ItemBuilder
import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.api.logs.Log
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material

class LogResultsGUI(user: User, results: List<Log>) : PaginatedInventory(
    user,
    MiniMessage.miniMessage().deserialize("<blue>Log Results"),
    InventorySize.SIX_ROWS
) {
    override val listToIterate: List<Pair<ItemBuilder, Int>> = results.map {
        Pair(
            ItemBuilder.builder()
                .material(Material.PAPER)
                .name(MiniMessage.miniMessage().deserialize("<green>Log: <white>${PlayerTracerPlugin.instance.logsProvider.getId(it::class)}")),
            0
        )
    }

    companion object {
        val glassSlots = intArrayOf(
            0,  1,  2,  3,  4,  5,  6,  7,  8,
            9,                              17,
            18,                             26,
            27,                             35,
            36,                             44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
        )

        val logSlots = (0..53).filter {
            it !in glassSlots
        }
    }

    override fun setupInventory() {
        var stack = ItemBuilder.builder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name(Component.text(" "))
            .build()

        for (slot in glassSlots) {
            inventory.setItem(slot, stack)
        }

        fillWithLogs()
    }

    fun fillWithLogs() {
        val toIndex = if (currentPage == 0) logSlots.size else (currentPage + 1) * logSlots.size

        val logs = listToIterate.subList(
            if (currentPage == 0) 0 else currentPage * logSlots.size,
            if (toIndex > listToIterate.size) listToIterate.size else toIndex
        )

        for (i in logs.indices) {
            val (item, _) = logs[i]
            inventory.setItem(logSlots[i], item.build())
        }
    }
}