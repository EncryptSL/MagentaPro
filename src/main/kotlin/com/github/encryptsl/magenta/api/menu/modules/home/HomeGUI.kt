package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.glow
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.magenta.Magenta
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import fr.euphyllia.energie.model.SchedulerType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player

class HomeGUI(private val magenta: Magenta) {

    //private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    //private val simpleMenu = menuUI.SimpleMenu(magenta)
    private val homeEditorGUI: HomeEditorGUI by lazy {
        HomeEditorGUI(magenta, this)
    }

    fun openHomeGUI(player: Player) {
        val gui = Gui.of(magenta.homeMenuConfig.getConfig().getInt("menu.gui.size", 6)).title(
            ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.gui.display").toString())
        )

        //menuUI.useAllFillers(gui.filler, magenta.homeMenuConfig.getConfig())

        gui.component { component ->
            component.render { container, viewer ->
                magenta.homeModel.getHomesByOwner(viewer.uniqueId).thenAccept { homes ->
                    Magenta.scheduler.runTask(SchedulerType.SYNC) {
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
                                    Placeholder.parsed("pitch", home.value.pitch.toString()),
                                )) }.toMutableList()


                            val itemHomeBuilder = createItem(material) {
                                amount = 1
                                meta {
                                    setNameComponent = itemNameComponent
                                    setLoreComponentList = loresComponents
                                    glow = true
                                }
                            }

                            val item = ItemBuilder.from(itemHomeBuilder).asGuiItem { player, context ->
                                //if (context.isLeftClick) {
                                //    magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home.value.homeName, magenta.config.getLong("teleport-cooldown")))
                                //    return@asGuiItem
                                //}
                                //if (context.isRightClick) {
                                //    //simpleMenu.clickSound(player, magenta.homeEditorConfig.getConfig())
                                //    homeEditorGUI.openHomeEditorGUI(player, home.value.homeName)
                                //}
                            }
                            container.set(home.index, item)
                        }
                    }
                }
            }
        }.build().open(player)
    }
}