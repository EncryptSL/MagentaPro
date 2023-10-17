package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.extensions.positionIndexed
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException

@Suppress("UNUSED")
@CommandDescription("Provided by plugin EncryptSL")
class LevelCmd(private val magenta: Magenta) {

    private val commandHelper = CommandHelper(magenta)

    @CommandMethod("level")
    fun onLevel(player: Player) {

        try {
            val data = magenta.virtualLevel.getLevel(player.uniqueId)
            val level = data.level
            val currentExp = data.experience
            commandHelper.showLevelProgress(player, level, currentExp)
        } catch (e : IllegalArgumentException) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"),
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @CommandMethod("level <player>")
    fun onLevelOther(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        try {
            val data = magenta.virtualLevel.getLevel(target.uniqueId)
            val level = data.level
            val currentExp = data.experience
            commandHelper.showLevelProgress(commandSender, level, currentExp)
        } catch (e : IllegalArgumentException) {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"),
                Placeholder.parsed("exception", e.message ?: e.localizedMessage)
            ))
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

    @ProxiedBy("toplevels")
    @CommandMethod("levelstop")
    fun onLevelTop(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.top.header")))
        magenta.virtualLevel.getLevels(10).toList().positionIndexed { k, v ->
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.top"), TagResolver.resolver(
                Placeholder.parsed("position", k.toString()),
                Placeholder.parsed("player", v.first),
                Placeholder.parsed("level", v.second.toString()),
            )))
        }
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.top.footer")))
    }

    @CommandMethod("levels add <player> <amount> level")
    @CommandPermission("magenta.levels.add.level")
    fun onLevelAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {

        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.addLevel(target.uniqueId, amount)
        target.player?.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.add"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
        commandSender.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.add.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
    }

    @CommandMethod("levels set <player> <amount> level")
    @CommandPermission("magenta.level.set.level")
    fun onLevelSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.setLevel(target.uniqueId, amount)
        magenta.virtualLevel.setExperience(target.uniqueId, 0)
        target.player?.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.set"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
        commandSender.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.set.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
    }

    @CommandMethod("levels add <player> <amount> points")
    @CommandPermission("magenta.levels.experience.add")
    fun onLevelPointsAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))
        magenta.virtualLevel.addExperience(target.uniqueId, amount)
        target.player?.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.add"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
            )))
        commandSender.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.add.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
            )))
    }

    @CommandMethod("levels set <player> <amount> points")
    @CommandPermission("magenta.levels.set.points")
    fun onLevelPointsSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.setExperience(target.uniqueId, amount)
        target.player?.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.set"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
            )))
        commandSender.sendMessage(ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.set.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
        )))
    }

}