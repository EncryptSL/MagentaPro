package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import fr.euphyllia.energie.model.SchedulerType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player

class HomeGUI(private val magenta: Magenta) {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val simpleMenu = menuUI.SimpleMenu(magenta)
    private val homeEditorGUI: HomeEditorGUI by lazy {
        HomeEditorGUI(
            magenta,
            this
        )
    }

    fun openHomeGUI(player: Player) {
        val gui = simpleMenu.simpleGui(
            magenta.homeMenuConfig.getConfig().getString("menu.gui.display").toString(),
            magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST)

        menuUI.useAllFillers(gui.filler, magenta.homeMenuConfig.getConfig())

        magenta.homeModel.getHomesByOwner(player.uniqueId).thenAccept { homes ->
            Magenta.scheduler.runTask(SchedulerType.SYNC) {
                for (home in homes) {
                    val material = Material.getMaterial(home.homeIcon) ?: Material.OAK_DOOR

                    val itemHomeBuilder = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

                    if (magenta.homeMenuConfig.getConfig().contains("menu.home-info.display")) {
                        itemHomeBuilder.setName(
                            ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.home-info.display").toString(),
                                Placeholder.parsed("home", home.homeName)
                            ))
                    }

                    if (magenta.homeMenuConfig.getConfig().contains("menu.home-info.lore")) {
                        val lores = magenta.homeMenuConfig.getConfig()
                            .getStringList("menu.home-info.lore")
                            .map { ModernText.miniModernText(it, TagResolver.resolver(
                                Placeholder.parsed("home", home.homeName),
                                Placeholder.parsed("world", home.world),
                                Placeholder.parsed("x", home.x.toString()),
                                Placeholder.parsed("y", home.y.toString()),
                                Placeholder.parsed("z", home.z.toString()),
                                Placeholder.parsed("yaw", home.yaw.toString()),
                                Placeholder.parsed("pitch", home.pitch.toString()),
                            )) }
                            .toMutableList()
                        itemHomeBuilder.addLore(lores)
                    }

                    val item = ItemBuilder.from(itemHomeBuilder.setGlowing(true).create()).asGuiItem { action ->
                        if (action.isLeftClick) {
                            magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home.homeName, magenta.config.getLong("teleport-cooldown")))
                            return@asGuiItem
                        }
                        if (action.isRightClick) {
                            simpleMenu.clickSound(player, magenta.homeEditorConfig.getConfig())
                            homeEditorGUI.openHomeEditorGUI(player, home.homeName)
                        }
                    }
                    gui.addItem(item)
                }
            }
        }
        gui.open(player)
    }
}