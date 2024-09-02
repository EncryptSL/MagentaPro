package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.extensions.experienceFormula
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class LevelsCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("levels add <player> <amount> level")
    @Permission("magenta.levels.add.level")
    @CommandDescription("This command add to other player levels")
    fun onLevelAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (amount >= magenta.config.getInt("level.level_limit"))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.reached"))

        magenta.levelAPI.getUserByUUID(target.uniqueId).thenAccept {
            if (it.level >= magenta.config.getInt("level.level_limit"))
                return@thenAccept commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.reached"))

            magenta.levelAPI.addLevel(target.uniqueId, amount)
            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.add",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.add.self.other",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                    Placeholder.parsed("player", target.name.toString()))
            )

            return@exceptionally null
        }.join()
    }

    @Command("levels set <player> <amount> level")
    @Permission("magenta.levels.set.level")
    @CommandDescription("This command set to other player levels")
    fun onLevelSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (amount > magenta.config.getInt("level.level_limit"))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.reached"))

        magenta.levelAPI.getUserByUUID(target.uniqueId).thenAccept {
            if (it.level >= magenta.config.getInt("level.level_limit") && amount > 1)
                return@thenAccept commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.reached"))

            magenta.levelAPI.setLevel(target.uniqueId, amount)
            magenta.levelAPI.setExperience(target.uniqueId, 0)
            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.set",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.set.self.other",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString())
                    )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                    Placeholder.parsed("player", target.name.toString())
                ))

            return@exceptionally null
        }.join()
    }

    @Command("levels add <player> <amount> points [silent]")
    @Permission("magenta.levels.experience.add")
    @CommandDescription("This command add to other player points")
    fun onLevelPointsAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int,
        @Argument(value = "silent") @Default("false") silent: Boolean
    ) {

        if (amount >= experienceFormula(magenta.config.getInt("level.level_limit")))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.points.reached"))

        magenta.levelAPI.getUserByUUID(target.uniqueId).thenAccept {
            if (it.experience >= experienceFormula(magenta.config.getInt("level.level_limit")))
                return@thenAccept commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.points.reached"))

            magenta.levelAPI.addExperience(target.uniqueId, amount)
            commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.success.experience.add.self.other", TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()))
            ))

            if (silent) {
                target.player?.sendMessage(magenta.locale.translation("magenta.command.levels.success.experience.add.silent",
                        Placeholder.parsed("experience", amount.toString())
                ))

                return@thenAccept
            }

            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.experience.add",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("experience", amount.toString()),
                    )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                    Placeholder.parsed("player", target.name.toString())
                ))

            return@exceptionally null
        }.join()
    }

    @Command("levels set <player> <amount> points")
    @Permission("magenta.levels.set.experience")
    @CommandDescription("This command set to other player points")
    fun onLevelPointsSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {

        if (amount > experienceFormula(magenta.config.getInt("level.level_limit")))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.points.reached"))

        magenta.levelAPI.getUserByUUID(target.uniqueId).thenAccept {
            if (it.experience >= experienceFormula(magenta.config.getInt("level.level_limit")))
                return@thenAccept commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.error.max.level.points.reached"))

            magenta.levelAPI.setExperience(target.uniqueId, amount)
            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.experience.set",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("experience", amount.toString()),
                    )))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.experience.set.self.other",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("experience", amount.toString()),
                    )))
        }.exceptionally {
            commandSender.sendMessage(magenta.locale.translation("magenta.command.level.error.not.account",
                Placeholder.parsed("player", target.name.toString())
            ))

            return@exceptionally null
        }.join()
    }

}