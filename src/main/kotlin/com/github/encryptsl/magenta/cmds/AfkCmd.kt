package com.github.encryptsl.magenta.cmds

import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class AfkCmd(private val magenta: Magenta) {
    @Command("afk")
    @Permission("magenta.afk")
    fun onAfk(player: Player) {
        magenta.afk.toggleAfk(player.uniqueId)
    }
    @Command("afk <player>")
    @Permission("magenta.afk.other")
    fun onAfkOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        magenta.afk.toggleAfk(target.uniqueId)
    }

}