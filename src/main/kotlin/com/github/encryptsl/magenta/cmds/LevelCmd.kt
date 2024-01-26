package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.extensions.positionIndexed
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class LevelCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @Command("level")
    @Permission("magenta.level")
    fun onLevel(player: Player) {

        try {
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(player.uniqueId)
            commandHelper.showLevelProgress(player, level, experience)
        } catch (e : IllegalArgumentException) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"),
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @Command("level <player>")
    @Permission("magenta.level.other")
    fun onLevelOther(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        try {
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(target.uniqueId)
            commandHelper.showLevelProgress(commandSender, level, experience)
        } catch (e : IllegalArgumentException) {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"),
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @ProxiedBy("toplevels")
    @Command("leveltop")
    @Permission("magenta.level.top")
    fun onLevelTop(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.top.header")))
        magenta.virtualLevel.getLevels(10).toList().sortedByDescending { a -> a.second }.positionIndexed { k, v ->
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.top"), TagResolver.resolver(
                Placeholder.parsed("position", k.toString()),
                Placeholder.parsed("player", v.first),
                Placeholder.parsed("level", v.second.toString()),
            )))
        }
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.top.footer")))
    }

}