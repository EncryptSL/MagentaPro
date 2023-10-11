package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class AfkCmd(private val magenta: Magenta) {
    @CommandMethod("afk")
    @CommandPermission("magenta.afk")
    fun onAfk(player: Player) {
        if (magenta.afk.isAfk(player.uniqueId)) {
            magenta.afk.forceTime(player.uniqueId, magenta.config.getLong("auto-afk"))
        } else {
            magenta.afk.forceTime(player.uniqueId, 0)
        }
    }
    @CommandMethod("afk <player>")
    @CommandPermission("magenta.afk.other")
    fun onAfkOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        if (magenta.afk.isAfk(target.uniqueId)) {
            magenta.afk.forceTime(target.uniqueId, magenta.config.getLong("auto-afk"))
        } else {
            magenta.afk.forceTime(target.uniqueId, 0)
        }
    }

}