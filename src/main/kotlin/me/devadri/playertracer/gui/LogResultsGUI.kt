package me.devadri.playertracer.gui

import me.devadri.obsidian.inventory.InventorySize
import me.devadri.obsidian.item.ItemBuilder
import me.devadri.obsidian.menu.button.MenuButton
import me.devadri.obsidian.menu.coords.Coordinates
import me.devadri.obsidian.menu.paginated.PaginatedMenu
import me.devadri.obsidian.toUser
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.location.Location
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.util.miniMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

class LogResultsGUI(results: List<Log>) : PaginatedMenu<Log>(
    results,
    miniMessage("<blue>Log Results"),
    PlayerTracerPlugin.instance,
    InventorySize.SIX_ROWS
) {

    override val slots: Array<Int> = (0..53).filter {
        it !in glassSlots
    }.toTypedArray()

    val totalPages = list.size / slots.size

    override val nextPageButton: MenuButton = MenuButton(
        plugin,
        ItemBuilder.builder()
            .material(Material.ARROW)
            .name(miniMessage("<aqua>Next Page")),
        NEXT_PAGE_COORDS
    ) { _, _, _ ->
        if (!hasNextPage()) return@MenuButton

        currentPage++
        setup()
    }

    override val previousPageButton: MenuButton = MenuButton(
        plugin,
        ItemBuilder.builder()
            .material(Material.ARROW)
            .name(miniMessage("<aqua>Previous Page")),
        PREVIOUS_PAGE_COORDS
    ) { _, _, _ ->
        if (!hasPreviousPage()) return@MenuButton

        currentPage--
        setup()
    }

    override val currentPageButton: MenuButton
        get() = MenuButton(
            plugin,
            ItemBuilder.builder()
                .material(Material.PAPER)
                .name(miniMessage("<aqua>Current Page: <gold>${currentPage + 1} / ${totalPages + 1}")),
            CURRENT_PAGE_COORDS
        )

    override val buttonForT: (Log) -> MenuButton = { log ->
        MenuButton(
            plugin,
            log.guiItem(
                ItemBuilder.builder()
                    .material(Material.WRITABLE_BOOK)
                    .name(miniMessage(PlayerTracerPlugin.instance.getLogDisplayName(log::class)))
                    .lore(
                        listOf(
                            "<gold>Message</gold><gray>:</gray> <white>${log.message}",
                            "<gold>Player UUID</gold><gray>:</gray> <white>${log.playerUUID}",
                            "<gold>Location</gold><gray>:</gray> <white>${log.location.worldName} / ${log.location.x}x / ${log.location.y}y / ${log.location.z}z",
                        ).map { msg -> miniMessage(msg) }
                    ).addPersistentData(
                        NamespacedKey(plugin, "location"),
                        Location.PERSISTENT_DATA_TYPE,
                        log.location
                    ),
                log
            ).addToLore(
                listOf(
                    Component.empty(),
                    miniMessage("<blue>Click to teleport</blue>")
                )
            ),
            Coordinates(0, 0)
        ) { e, _, _ ->
            val location = e.currentItem!!.itemMeta.persistentDataContainer.get(
                NamespacedKey(PlayerTracerPlugin.instance, "location"),
                Location.PERSISTENT_DATA_TYPE
            ) ?: return@MenuButton

            val user = (e.whoClicked as? Player)?.toUser() ?: return@MenuButton

            plugin.commandManager.getCommandOrNull("playertracer")?.subCommands?.firstOrNull {
                it.info.name == "tp"
            }?.execute(
                user,
                arrayOf(location.worldName, "${location.x}", "${location.y}", "${location.z}"),
                "playertracer"
            )
        }
    }

    companion object {
        val glassSlots = intArrayOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
        )

        val PREVIOUS_PAGE_COORDS = Coordinates(5, 3)
        val CURRENT_PAGE_COORDS = Coordinates(5, 4)
        val NEXT_PAGE_COORDS = Coordinates(5, 5)
    }

    override fun setup() {
        // Temp workaround
        buttons.clear()

        val stack = ItemBuilder.builder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name(Component.text(" "))
            .build()

        for (slot in glassSlots) {
            inventory.setItem(slot, stack)
        }

        super.setup()
    }
}