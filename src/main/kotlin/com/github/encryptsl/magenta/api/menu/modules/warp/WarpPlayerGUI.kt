package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.GuiItem
import fr.euphyllia.energie.model.SchedulerType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class WarpPlayerGUI(private val magenta: Magenta, warpGUI: WarpGUI, private val playerEditorGUI: WarpPlayerEditorGUI) : MenuExtender {

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

                    val itemHomeBuilder = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

                    if (magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.display")) {
                        itemHomeBuilder.setName(
                            ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                                Placeholder.parsed("warp", warp.warpName)
                            ))
                    }

                    if (magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.lore")) {
                        val lores = magenta.warpPlayerMenuConfig.getConfig()
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
                            .toMutableList()
                        itemHomeBuilder.addLore(lores)
                    }
                    gui.addItem(getItem(player, itemHomeBuilder, warp))
                }
            }
        }
        paginationMenu.paginatedControlButtons(player, magenta.warpPlayerMenuConfig.getConfig(), gui)

        gui.open(player)
    }

    private fun getItem(humanEntity: HumanEntity, itemBuilder: com.github.encryptsl.magenta.api.ItemBuilder, warp: WarpEntity): GuiItem {
        return ItemBuilder.from(itemBuilder.setGlowing(true).create()).asGuiItem { action ->
            if (action.isLeftClick) {
                humanEntity.teleport(magenta.warpModel.toLocation(warp.warpName))
                return@asGuiItem
            }

            if (action.isRightClick) {
                playerEditorGUI.openWarpPlayerEditor(action.whoClicked, warp.warpName)
            }
        }
    }
}