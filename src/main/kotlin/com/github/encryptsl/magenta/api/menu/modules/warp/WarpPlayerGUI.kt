package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import org.bukkit.entity.Player

class WarpPlayerGUI(private val magenta: Magenta, warpGUI: WarpGUI, private val playerEditorGUI: WarpPlayerEditorGUI) : Menu {

    override fun open(player: Player) {
        TODO("Not yet implemented")
    }

    /*
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val paginationMenu = menuUI.PaginationMenu(magenta, warpGUI)

    override fun openMenu(player: HumanEntity) {
        val gui = paginationMenu.paginatedGui(
            ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("count", magenta.warpModel.getWarpsByOwner(player.uniqueId).join().count().toString())
            ),
            magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        )

        menuUI.useAllFillers(gui.filler, magenta.warpPlayerMenuConfig.getConfig())

        gui.setDefaultClickAction { el ->
            if (el.currentItem != null && el.isLeftClick || el.isRightClick) {
                paginationMenu.clickSound(el.whoClicked, magenta.warpPlayerMenuConfig.getConfig())
            }
        }

        magenta.warpModel.getWarpsByOwner(player.uniqueId).thenAccept { playerWarps ->
            Magenta.scheduler.runTask(SchedulerType.SYNC) {
                for (warp in playerWarps) {
                    val material = Material.getMaterial(warp.warpIcon) ?: Material.OAK_SIGN

                    if (!magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.display")) continue
                    if (!magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.lore")) continue

                    val displayItemName = ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                        Placeholder.parsed("warp", warp.warpName)
                    )

                    val lore = magenta.warpPlayerMenuConfig.getConfig()
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
                            setNameComponent = displayItemName
                            setLoreComponentList = lore
                            glow = true
                        }
                    }

                    gui.addItem(getItem(player, itemHomeBuilder, warp))
                }
            }
        }
        paginationMenu.paginatedControlButtons(player, magenta.warpPlayerMenuConfig.getConfig(), gui)

        gui.open(player)
    }

    private fun getItem(humanEntity: HumanEntity, itemStack: ItemStack, warp: WarpEntity): GuiItem {
        return ItemBuilder.from(itemStack).asGuiItem { action ->
            if (action.isLeftClick) {
                humanEntity.teleport(magenta.warpModel.toLocation(warp.warpName))
                return@asGuiItem
            }

            if (action.isRightClick) {
                playerEditorGUI.openWarpPlayerEditor(action.whoClicked, warp.warpName)
            }
        }
    }*/
}