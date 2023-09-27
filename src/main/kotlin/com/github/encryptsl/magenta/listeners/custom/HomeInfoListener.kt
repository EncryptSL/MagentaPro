package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.home.HomeInfoEvent
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HomeInfoListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onHomeInfo(event: HomeInfoEvent) {
        val player = event.player
        val infoType = event.infoType

        when(infoType) {
            InfoType.LIST -> {
                val list = magenta.homeModel.getHomesByOwner(player).joinToString { s ->
                    magenta.localeConfig.getMessage("magenta.command.home.success.list.component").replace("<home>", s.homeName)
                }
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.list"), TagResolver.resolver(
                    Placeholder.component("homes", ModernText.miniModernText(list))
                )))
            }
            InfoType.INFO -> {
                val homeName = event.homeName ?: return
                if (!magenta.homeModel.getHomeExist(player, homeName))
                    return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist")))

                magenta.config.getStringList("home-info-format").forEach { s ->
                    player.sendMessage(ModernText.miniModernText(s, TagResolver.resolver(
                        Placeholder.parsed("home", magenta.homeModel.getHome(homeName, HomeTable.home)),
                        Placeholder.parsed("owner", magenta.homeModel.getHome(homeName, HomeTable.username)),
                        Placeholder.parsed("world", magenta.homeModel.getHome(homeName, HomeTable.world)),
                        Placeholder.parsed("x", magenta.homeModel.getHome(homeName, HomeTable.x).toString()),
                        Placeholder.parsed("y", magenta.homeModel.getHome(homeName, HomeTable.y).toString()),
                        Placeholder.parsed("z", magenta.homeModel.getHome(homeName, HomeTable.z).toString()),
                    )))
                }
            }
        }
    }

}