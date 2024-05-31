package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import com.github.encryptsl.magenta.common.database.entity.WarpEntity
import dev.triumphteam.gui.item.GuiItem
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WarpPlayerGUI(private val magenta: Magenta, warpGUI: WarpGUI, private val playerEditorGUI: WarpPlayerEditorGUI) : Menu {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    override fun open(player: Player) {
        val rows = magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)

        val gui = Gui.of(rows).title(
            ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("count", magenta.warpModel.getWarpsByOwner(player.uniqueId).join().count().toString())
            )
        )

        gui.component { component ->
            component.render { container, _ ->
                menuUI.useAllFillers(rows, container, magenta.warpPlayerMenuConfig.getConfig())
            }
            component.render { container, viewer ->
                val playerWarps = magenta.warpModel.getWarpsByOwner(viewer.uniqueId).join()
                for (warp in playerWarps.withIndex()) {
                    val material = Material.getMaterial(warp.value.warpIcon) ?: Material.OAK_SIGN

                    if (!magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.display")) continue
                    if (!magenta.warpPlayerMenuConfig.getConfig().contains("menu.warp-info.lore")) continue

                    val displayItemName = ModernText.miniModernText(magenta.warpPlayerMenuConfig.getConfig().getString("menu.warp-info.display").toString(),
                        Placeholder.parsed("warp", warp.value.warpName)
                    )

                    val lore = magenta.warpPlayerMenuConfig.getConfig()
                        .getStringList("menu.warp-info.lore")
                        .map { ModernText.miniModernText(it, TagResolver.resolver(
                            Placeholder.parsed("owner", warp.value.owner),
                            Placeholder.parsed("warp", warp.value.warpName),
                            Placeholder.parsed("world", warp.value.world),
                            Placeholder.parsed("x", warp.value.x.toString()),
                            Placeholder.parsed("y", warp.value.y.toString()),
                            Placeholder.parsed("z", warp.value.z.toString()),
                            Placeholder.parsed("yaw", warp.value.yaw.toString()),
                            Placeholder.parsed("pitch", warp.value.pitch.toString()),
                        )) }

                    val itemBuilder = com.github.encryptsl.kmono.lib.utils.ItemBuilder(material).setName(displayItemName)
                        .addLore(lore.toMutableList())
                        .setGlowing(true).create()

                    container.set(warp.index, getItem(itemBuilder, warp.value))
                }
            }
        }.build().open(player)
    }

    private fun getItem(itemStack: ItemStack, warp: WarpEntity): GuiItem<Player, ItemStack> {
        return ItemBuilder.from(itemStack).asGuiItem { player, context ->
                player.teleport(magenta.warpModel.toLocation(warp.warpName))
            //if (context.isRightClick) {
            //    playerEditorGUI.openWarpPlayerEditor(player, warp.warpName)
            //}
        }
    }
}