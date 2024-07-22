package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.GuiItem
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Suppress("UNUSED")
class WarpPlayerGUI(private val magenta: Magenta, private val warpGUI: WarpGUI, private val playerEditorGUI: WarpPlayerEditorGUI) : Menu {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    override fun open(player: Player) {
        val rows = magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        val playerWarps = magenta.warpModel.getWarpsByOwner(player.uniqueId).join()
        val warpsIterator = playerWarps.iterator()

        val gui = menuUI.paginatedBuilderGui(rows,
            ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("count", playerWarps.count().toString())
            ), magenta.homeMenuConfig.getConfig()
        )

        menuUI.useAllFillers(gui, magenta.warpPlayerMenuConfig.getConfig())

        if (!magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.display")) return
        if (!magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.lore")) return

        while (warpsIterator.hasNext()) {
            val warp = warpsIterator.next()
            val material = Material.getMaterial(warp.warpIcon) ?: Material.OAK_SIGN

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

            val itemBuilder = ItemCreator(material).setName(displayItemName)
                .addLore(lore.toMutableList())
                .setGlowing(true).create()

            gui.addItem(getItem(itemBuilder, warp))
        }
        menuUI.pagination(player, gui, magenta.warpPlayerMenuConfig.getConfig(), warpGUI)
        gui.open(player)
    }

    private fun getItem(itemStack: ItemStack, warp: WarpEntity): GuiItem {
        return ItemBuilder.from(itemStack).asGuiItem { context ->
            if (context.isLeftClick) {
                context.whoClicked.teleport(magenta.warpModel.toLocation(warp.warpName))
                return@asGuiItem
            }
            if (context.isRightClick) {
                playerEditorGUI.openWarpPlayerEditor(context.whoClicked as Player, warp.warpName)
                return@asGuiItem
            }
        }
    }
}