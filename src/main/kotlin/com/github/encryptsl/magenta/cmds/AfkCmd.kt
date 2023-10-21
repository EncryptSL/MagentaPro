package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class AfkCmd(private val magenta: Magenta) {
    @CommandMethod("afk")
    @CommandPermission("magenta.afk")
    fun onAfk(player: Player) {
        if (magenta.afk.isAfk(player.uniqueId)) {
            magenta.afk.forceAfk(player.uniqueId, false)
        } else {
            magenta.afk.forceAfk(player.uniqueId, true)
        }
    }
    @CommandMethod("afk <player>")
    @CommandPermission("magenta.afk.other")
    fun onAfkOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        if (magenta.afk.isAfk(target.uniqueId)) {
            magenta.afk.forceAfk(target.uniqueId, false)
        } else {
            magenta.afk.forceAfk(target.uniqueId, true)
        }
    }

}