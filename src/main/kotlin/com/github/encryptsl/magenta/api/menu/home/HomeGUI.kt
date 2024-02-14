package com.github.encryptsl.magenta.api.menu.home

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class HomeGUI(private val magenta: Magenta) {

    private val shopUI: ShopUI by lazy { ShopUI(magenta) }

    fun openHomeGUI(player: Player) {
        val gui = shopUI.simpleGui(magenta.homeMenuConfig.getConfig().getString("menu.display").toString(), 6, GuiType.CHEST)
        magenta.homeModel.getHomesByOwner(player).forEach { home ->

            val material = Material.getMaterial(home.homeIcon) ?: Material.OAK_DOOR

            val itemHomeBuilder = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

            if (magenta.homeMenuConfig.getConfig().contains("menu.home-info.display")) {
                itemHomeBuilder.setName(ModernText.miniModernText(magenta.homeMenuConfig.getConfig().getString("menu.home-info.display").toString(),
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

            val item = ItemBuilder.from(itemHomeBuilder.setGlowing(true).create()).asGuiItem {action ->
                if (action.isLeftClick) {
                    player.teleport(magenta.homeModel.toLocation(player, home.homeName), PlayerTeleportEvent.TeleportCause.PLUGIN)
                }
            }

            gui.addItem(item)
            gui.open(player)
        }
    }
}