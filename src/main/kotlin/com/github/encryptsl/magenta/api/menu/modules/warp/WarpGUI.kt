package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import org.bukkit.entity.Player

class WarpGUI(private val magenta: Magenta) : Menu {

    /*
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, this, WarpPlayerEditorGUI(magenta)) }
     */
    override fun open(player: Player) {
        /*
        val gui = paginationMenu.paginatedGui(
            ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.gui.display").toString(), Placeholder.parsed("count", magenta.warpModel.getWarps().join().count().toString())),
            magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        )
        menuUI.useAllFillers(gui.filler, magenta.warpMenuConfig.getConfig())

        gui.setDefaultClickAction { el ->
            if (el.currentItem != null && el.isLeftClick || el.isRightClick) {
                paginationMenu.clickSound(el.whoClicked, magenta.warpMenuConfig.getConfig())
            }
        }

        magenta.warpModel.getWarps().thenAccept { warps ->
            Magenta.scheduler.runTask(SchedulerType.SYNC) {
                for (warp in warps) {
                    val material = Material.getMaterial(warp.warpIcon) ?: Material.OAK_SIGN

                    if (!magenta.warpMenuConfig.getConfig().contains("menu.warp-info.display")) continue
                    if (!magenta.warpMenuConfig.getConfig().contains("menu.warp-info.lore")) continue

                    val itemComponentName = ModernText.miniModernText(magenta.warpMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                        Placeholder.parsed("warp", warp.warpName)
                    )

                    val lore = magenta.warpMenuConfig.getConfig()
                        .getStringList("menu.warp-info.lore")
                        .map { ModernText.miniModernText(it, TagResolver.resolver(
                            Placeholder.parsed("owner", warp.owner),
                            Placeholder.parsed("warp", warp.warpName),
                            Placeholder.parsed("world", warp.world),
                            Placeholder.parsed("x", warp.x.toString()),
                            Placeholder.parsed("y", warp.y.toString()),
                            Placeholder.parsed("z", warp.z.toString()),
                            Placeholder.parsed("yaw", warp.yaw.toString()),
                            Placeholder.parsed("pitch", warp.pitch.toString()),
                        )) }

                    val itemHomeBuilder = createItem(material) {
                        amount = 1
                        meta {
                            setNameComponent = itemComponentName
                            setLoreComponentList = lore
                        }
                    }

                    val item = ItemBuilder.from(itemHomeBuilder).asGuiItem { action ->
                        if (action.isLeftClick || action.isRightClick) {
                            player.teleport(magenta.warpModel.toLocation(warp.warpName))
                            return@asGuiItem
                        }
                    }
                    gui.addItem(item)
                }
            }
        }

        paginationMenu.paginatedControlButtons(player, magenta.warpMenuConfig.getConfig(), gui)
        actionCustomButtons(player, magenta.warpMenuConfig.getConfig(), gui)

        gui.open(player)
         */
    }

    /*
    private fun actionCustomButtons(player: HumanEntity, config: FileConfiguration, gui: PaginatedGui) {

        for (el in config.getConfigurationSection("menu.items.buttons")?.getKeys(false)!!) {
            val material = Material.getMaterial(config.getString("menu.items.buttons.${el}.icon").toString()) ?: continue
            if (config.contains("menu.items.buttons.$el")) {
                if (!config.contains("menu.items.buttons.$el.name"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", config.name)
                    ))

                val lores = config
                    .getStringList("menu.items.buttons.$el.lore")
                    .map { ModernText.miniModernText(it) }

                val itemStack = createItem(material) {
                    amount = 1
                    meta {
                        setNameComponent = ModernText.miniModernText(config.getString("menu.items.buttons.${el}.name").toString())
                        setLoreComponentList = lores
                    }
                }

                val actionItem = getItem(player, itemStack, config, el)

                gui.setItem(config.getInt("menu.items.buttons.$el.positions.row"), config.getInt("menu.items.buttons.$el.positions.col"), actionItem)
            }
        }
    }

    private fun getItem(
        humanEntity:
        HumanEntity,
        itemStack: ItemStack,
        config: FileConfiguration,
        el: String
    ): GuiItem {
        return ItemBuilder.from(itemStack).asGuiItem { action ->
            if (action.isLeftClick) {
                openOwnerWarps(humanEntity, config, el)
            }
        }
    }

    private fun openOwnerWarps(
        player: HumanEntity,
        fileConfiguration: FileConfiguration,
        el: String
    ) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("OPEN_MENU", true)) {
            warpPlayerGUI.openMenu(player)
        }
    }*/

}