package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

@Suppress("UNUSED")
class BackCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("back")
    @Permission("magenta.back")
    @CommandDescription("This command teleport you back on your previous location.")
    fun onBack(player: Player) {
        try {
            val userAccount = magenta.user.getUser(player.uniqueId)
            player.teleport(userAccount.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
            player.sendMessage(magenta.locale.translation("magenta.command.back.success"))
        } catch (e : Exception) {
            player.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    @Command("back <player>")
    @Permission("magenta.back.other")
    @CommandDescription("This command teleport other player back on his previous location.")
    fun onBack(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        try {
            onBack(target)
            commandSender.sendMessage(magenta.locale.translation("magenta.command.back.success.self.other",
                Placeholder.parsed("player", target.name)
            ))
        } catch (e : Exception) {
            commandSender.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }
}