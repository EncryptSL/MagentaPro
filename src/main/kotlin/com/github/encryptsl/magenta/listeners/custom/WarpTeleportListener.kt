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

        try {
            val (_: String, _: String, warp: String, world: String, x: Int, y: Int, z: Int, pitch: Float, yaw: Float) = magenta.warpModel.getWarps().first { s -> s.warpName == warpName}

            if (commandSender is Player) {
                if (target == null) {
                    commandSender.teleport(Location(Bukkit.getWorld(world), x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch))
                    commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.self"), TagResolver.resolver(
                        Placeholder.parsed("warp", warpName)
                    )))
                    return
                }

                if (commandSender.hasPermission("magenta.warp.other")) {
                    target.teleport(
                        Location(
                            Bukkit.getWorld(warp),
                            x.toDouble(),
                            y.toDouble(),
                            z.toDouble(),
                            yaw,
                            pitch
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
                    target.teleport(Location(Bukkit.getWorld(world), x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch))
                    target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.self"), TagResolver.resolver(
                        Placeholder.parsed("warp", warpName)
                    )))
                }
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.teleport.to"), TagResolver.resolver(
                    Placeholder.parsed("warp", warpName)
                )))
            }

        } catch (e : Exception) {
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }
}