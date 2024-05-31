package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
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
        val gui = Gui.of(rows).title(
            ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.gui.display").toString())
        )

        gui.component { component ->
            component.render { container, viewer ->
                menuUI.useAllFillers(rows, container, magenta.homeMenuConfig.getConfig())

                val homes = magenta.homeModel.getHomesByOwner(viewer.uniqueId).join()
                for (home in homes.withIndex()) {
                    val material = Material.getMaterial(home.value.homeIcon) ?: Material.OAK_DOOR
                    if (!magenta.homeMenuConfig.getConfig().contains("menu.home-info.display")) continue
                    if (!magenta.homeMenuConfig.getConfig().contains("menu.home-info.lore")) continue

                    val itemNameComponent = ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.home-info.display").toString(),
                        Placeholder.parsed("home", home.value.homeName)
                    )

                    val loresComponents = magenta.homeMenuConfig.getConfig()
                        .getStringList("menu.home-info.lore")
                        .map { ModernText.miniModernText(it, TagResolver.resolver(
                            Placeholder.parsed("home", home.value.homeName),
                            Placeholder.parsed("world", home.value.world),
                            Placeholder.parsed("x", home.value.x.toString()),
                            Placeholder.parsed("y", home.value.y.toString()),
                            Placeholder.parsed("z", home.value.z.toString()),
                            Placeholder.parsed("yaw", home.value.yaw.toString()),
                            Placeholder.parsed("pitch", home.value.pitch.toString()),)) }.toMutableList()


                    val itemBuilder = com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)
                        .setName(itemNameComponent)
                        .addLore(loresComponents).create()

                    val item = ItemBuilder.from(itemBuilder).asGuiItem { player, context ->
                        magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home.value.homeName, magenta.config.getLong("teleport-cooldown")))
                    //if (context.isRightClick) {
                    //    //simpleMenu.clickSound(player, magenta.homeEditorConfig.getConfig())
                    //    homeEditorGUI.openHomeEditorGUI(player, home.value.homeName)
                    //}
                    }
                    container.set(home.index, item)
                }
            }
        }.build().open(player)
    }
}