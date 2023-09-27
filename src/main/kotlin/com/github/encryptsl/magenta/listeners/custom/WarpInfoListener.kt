package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.warp.WarpInfoEvent
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpInfoListener(private val magenta: Magenta) : Listener {


    @EventHandler
    fun onWarpInfo(event: WarpInfoEvent) {
        val commandSender = event.commandSender
        val warpInfoType = event.infoType

        when(warpInfoType) {
            InfoType.LIST -> {
                val list = magenta.warpModel.getWarps().joinToString { s ->
                    magenta.localeConfig.getMessage("magenta.command.warp.success.list.component").replace("<warp>", s.warpName)
                }
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.list"), TagResolver.resolver(
                    Placeholder.component("warps", ModernText.miniModernText(list))
                )))
            }
            InfoType.INFO -> {
                val warpName = event.warpName ?: return
                if (!magenta.warpModel.getWarpExist(warpName))
                    return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist")))

                magenta.config.getStringList("warp-info-format").forEach { s ->
                    commandSender.sendMessage(ModernText.miniModernText(s, TagResolver.resolver(
                        Placeholder.parsed("warp", magenta.warpModel.getWarp(warpName, WarpTable.warpName)),
                        Placeholder.parsed("owner", magenta.warpModel.getWarp(warpName, WarpTable.username)),
                        Placeholder.parsed("world", magenta.warpModel.getWarp(warpName, WarpTable.world)),
                        Placeholder.parsed("x", magenta.warpModel.getWarp(warpName, WarpTable.x).toString()),
                        Placeholder.parsed("y", magenta.warpModel.getWarp(warpName, WarpTable.y).toString()),
                        Placeholder.parsed("z", magenta.warpModel.getWarp(warpName, WarpTable.z).toString()),
                    )))
                }
            }
        }
    }

}