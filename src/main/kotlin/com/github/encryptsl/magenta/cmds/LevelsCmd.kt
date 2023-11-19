package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class LevelsCmd(private val magenta: Magenta) {

    @CommandMethod("levels add <player> <amount> level")
    @CommandPermission("magenta.levels.add.level")
    fun onLevelAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {

        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.addLevel(target.uniqueId, amount)
        target.player?.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.add"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
        commandSender.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.add.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
    }

    @CommandMethod("levels set <player> <amount> level")
    @CommandPermission("magenta.levels.set.level")
    fun onLevelSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.setLevel(target.uniqueId, amount)
        magenta.virtualLevel.setExperience(target.uniqueId, 0)
        target.player?.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.set"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
        commandSender.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.level.set.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("level", amount.toString()),
            )))
    }

    @CommandMethod("levels add <player> <amount> points [silent]")
    @CommandPermission("magenta.levels.experience.add")
    fun onLevelPointsAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int,
        @Argument(value = "silent") silent: String?
    ) {
        val s = silent?.contains("-s") ?: false
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))
        magenta.virtualLevel.addExperience(target.uniqueId, amount)
        commandSender.sendMessage(
            ModernText.miniModernText(
                magenta.localeConfig.getMessage("magenta.command.levels.success.experience.add.to"),
                TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString()),
                    Placeholder.parsed("experience", amount.toString()),
                )))

        if (s) {
            target.player?.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.levels.success.experience.add.silent"),
                    Placeholder.parsed("experience", amount.toString())
                ))
            return
        }

        target.player?.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.add"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
            )))
    }

    @CommandMethod("levels set <player> <amount> points")
    @CommandPermission("magenta.levels.set.experience")
    fun onLevelPointsSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.level.error.not.account"),
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.setExperience(target.uniqueId, amount)
        target.player?.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.set"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
            )))
        commandSender.sendMessage(
            ModernText.miniModernText(
            magenta.localeConfig.getMessage("magenta.command.levels.success.experience.set.to"),
            TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()),
            )))
    }

}