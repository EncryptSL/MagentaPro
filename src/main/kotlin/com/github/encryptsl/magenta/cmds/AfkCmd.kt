package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED", "UNUSED_PARAMETER")
class AfkCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("afk")
    @Permission("magenta.afk")
    @CommandDescription("This command set you afk")
    fun onAfk(player: Player) {
        magenta.afk.isAfk(player.uniqueId, true)
    }
    @Command("afk <player>")
    @Permission("magenta.afk.other")
    @CommandDescription("This command set another player afk.")
    fun onAfkOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        magenta.afk.isAfk(target.uniqueId, true)
    }

}