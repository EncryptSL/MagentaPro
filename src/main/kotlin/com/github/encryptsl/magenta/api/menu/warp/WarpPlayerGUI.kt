package com.github.encryptsl.magenta.api.menu.warp

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.menu.MenuExtender
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player

class WarpPlayerGUI(private val magenta: Magenta, private val warpGUI: WarpGUI, private val playerEditorGUI: WarpPlayerEditorGUI) : MenuExtender {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    override fun openMenu(player: Player) {
        val gui = menuUI.paginatedGui(
            ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("count", magenta.warpModel.getWarpsByOwner(player).count().toString())
            ),
            magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        )

        menuUI.useAllFillers(gui.filler, magenta.warpPlayerMenuConfig.getConfig())

        magenta.warpModel.getWarpsByOwner(player).forEach { warp ->

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

            val item = ItemBuilder.from(itemHomeBuilder.setGlowing(true).create()).asGuiItem { action ->
                if (action.isLeftClick) {
                    player.teleport(magenta.warpModel.toLocation(warp.warpName))
                    return@asGuiItem
                }

                if (action.isRightClick) {
                    playerEditorGUI.openWarpPlayerEditor(player, warp.warpName)
                }
            }
            gui.addItem(item)
        }

        controlButtons(player, menuUI, magenta.warpPlayerMenuConfig, gui)

        gui.open(player)
    }

    private fun controlButtons(player: Player, menuUI: MenuUI, config: UniversalConfig, gui: PaginatedGui) {
        for (material in Material.entries) {
            menuUI.previousPage(player, material, config.getConfig(), "previous", gui)
            menuUI.closeButton(
                player,
                material,
                gui,
                config.getConfig(),
                warpGUI,
            )
            menuUI.nextPage(player, material, config.getConfig(), "next", gui)
        }
    }

}