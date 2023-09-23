package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.warp.WarpTeleportEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WarpTeleportListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onWarpTeleport(event: WarpTeleportEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val warpName = event.warpName

        if (!magenta.warpModel.getWarpExist(warpName))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("warp", warpName))))

        runCatching {
            magenta.warpModel.getWarps().first { s -> s.warpName == warpName }
        }.onSuccess { warp ->
            if (commandSender is Player) {
                if (target == null) {
                    commandSender.teleport(Location(Bukkit.getWorld(warp.warpName), warp.x.toDouble(), warp.y.toDouble(), warp.z.toDouble(), warp.yaw, warp.pitch))
                    commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.self"), TagResolver.resolver(
                        Placeholder.parsed("warp", warpName)
                    )))
                    return
                }

                if (commandSender.hasPermission("magenta.warp.other")) {
                    target.teleport(
                        Location(
                            Bukkit.getWorld(warp.warpName),
                            warp.x.toDouble(),
                            warp.y.toDouble(),
                            warp.z.toDouble(),
                            warp.yaw,
                            warp.pitch
                        )
                    )
                    target.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.self"),
                            TagResolver.resolver(
                                Placeholder.parsed("warp", warpName)
                            )
                        )
                    )
                    commandSender.sendMessage(
                        ModernText.miniModernText(
                            magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.to"),
                            TagResolver.resolver(
                                Placeholder.parsed("warp", warpName)
                            )
                        )
                    )
                }
            } else {
                if (target != null) {
                    target.teleport(Location(Bukkit.getWorld(warp.warpName), warp.x.toDouble(), warp.y.toDouble(), warp.z.toDouble(), warp.yaw, warp.pitch))
                    target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.self"), TagResolver.resolver(
                        Placeholder.parsed("warp", warpName)
                    )))
                }
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.to"), TagResolver.resolver(
                    Placeholder.parsed("warp", warpName)
                )))
            }
        }
    }
}