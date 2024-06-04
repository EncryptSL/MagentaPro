package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player

class HomeGUI(private val magenta: Magenta) {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val homeEditorGUI: HomeEditorGUI by lazy {
        HomeEditorGUI(magenta, this)
    }

    fun openHomeGUI(player: Player) {
        val rows = magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)
        val gui = menuUI.paginatedBuilderGui(rows,
            ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.gui.display").toString()),
            magenta.homeEditorConfig.getConfig()
        )

        menuUI.useAllFillers(gui, magenta.homeMenuConfig.getConfig())

        val homes = magenta.homeModel.getHomesByOwner(player.uniqueId).join()

        if (!magenta.homeMenuConfig.getConfig().contains("menu.home-info.display")) return
        if (!magenta.homeMenuConfig.getConfig().contains("menu.home-info.lore")) return

        for (home in homes) {
            val material = Material.getMaterial(home.homeIcon) ?: Material.RED_BED

            val itemNameComponent = ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.home-info.display").toString(),
                Placeholder.parsed("home", home.homeName)
            )

            val loresComponents = magenta.homeMenuConfig.getConfig()
                .getStringList("menu.home-info.lore")
                .map { ModernText.miniModernText(it, TagResolver.resolver(
                    Placeholder.parsed("home", home.homeName),
                    Placeholder.parsed("world", home.world),
                    Placeholder.parsed("x", home.x.toString()),
                    Placeholder.parsed("y", home.y.toString()),
                    Placeholder.parsed("z", home.z.toString()),
                    Placeholder.parsed("yaw", home.yaw.toString()),
                    Placeholder.parsed("pitch", home.pitch.toString()),))
                }


            val itemBuilder = com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)
                .setName(itemNameComponent)
                .addLore(loresComponents).create()

            gui.addItem(
                ItemBuilder.from(itemBuilder).asGuiItem { context ->
                    if (context.isLeftClick) {
                        magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home.homeName, magenta.config.getLong("teleport-cooldown")))
                        return@asGuiItem
                    }
                    if (context.isRightClick) {
                        homeEditorGUI.openHomeEditorGUI(player, home.homeName)
                        return@asGuiItem
                    }
                }
            )
        }

        menuUI.pagination(player, gui, magenta.homeMenuConfig.getConfig(), null)
        gui.open(player)
    }
}