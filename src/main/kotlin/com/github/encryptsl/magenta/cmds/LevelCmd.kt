package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.extensions.positionIndexed
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class LevelCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }


    @Command("level")
    @Permission("magenta.level")
    @CommandDescription("This command send your level progress")
    fun onLevel(player: Player) {
        try {
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(player.uniqueId)
            magenta.commandHelper.showLevelProgress(player, level, experience)
        } catch (e : IllegalArgumentException) {
            player.sendMessage(magenta.localeConfig.translation("magenta.exception",
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @Command("level <player>")
    @Permission("magenta.level.other")
    @CommandDescription("This command send other player level progress")
    fun onLevelOther(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.level.error.not.account",
                Placeholder.parsed("player", target.name.toString())
            ))

        try {
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(target.uniqueId)
            magenta.commandHelper.showLevelProgress(commandSender, level, experience)
        } catch (e : IllegalArgumentException) {
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.exception",
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @ProxiedBy("toplevels")
    @Command("leveltop")
    @Permission("magenta.level.top")
    @CommandDescription("This command send top players in levels")
    fun onLevelTop(commandSender: CommandSender) {
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.level.top.header"))
        magenta.virtualLevel.getLevels(10).toList().positionIndexed { k, v ->
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.level.top", TagResolver.resolver(
                Placeholder.parsed("position", k.toString()),
                Placeholder.parsed("player", v.first),
                Placeholder.parsed("level", v.second.toString()),
            )))
        }
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.level.top.footer"))
    }

}