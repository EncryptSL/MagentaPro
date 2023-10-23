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
        magenta.afk.toggleAfk(player.uniqueId)
    }
    @CommandMethod("afk <player>")
    @CommandPermission("magenta.afk.other")
    fun onAfkOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        magenta.afk.toggleAfk(target.uniqueId)
    }

}