package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.LegacyPaperCommandManager

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
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        magenta.virtualLevel.getUserByUUID(target.uniqueId).thenApply {
            magenta.virtualLevel.addLevel(target.uniqueId, amount)
            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.add",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.add.to",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                    Placeholder.parsed("player", target.name.toString()))
            )
        }
    }

    @Command("levels set <player> <amount> level")
    @Permission("magenta.levels.set.level")
    @CommandDescription("This command set to other player levels")
    fun onLevelSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.getUserByUUID(target.uniqueId).thenApply {
            magenta.virtualLevel.setLevel(target.uniqueId, amount)
            magenta.virtualLevel.setExperience(target.uniqueId, 0)
            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.set",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.level.set.to",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("level", amount.toString()),
                    )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                    Placeholder.parsed("player", target.name.toString())
                ))
        }
    }

    @Command("levels add <player> <amount> points")
    @Permission("magenta.levels.experience.add")
    @CommandDescription("This command add to other player points")
    fun onLevelPointsAdd(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int,
        @Flag(value = "silent", aliases = ["s"]) silent: Boolean
    ) {
        if (!magenta.virtualLevel.hasAccount(target.uniqueId))
            return commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                Placeholder.parsed("player", target.name.toString())
            ))

        magenta.virtualLevel.getUserByUUID(target.uniqueId).thenApply {
            magenta.virtualLevel.addExperience(target.uniqueId, amount)
            commandSender.sendMessage(magenta.locale.translation("magenta.command.levels.success.experience.add.to", TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("experience", amount.toString()))
            ))

            if (silent)
                return@thenApply target.player.let { it?.sendMessage(
                    magenta.locale.translation("magenta.command.levels.success.experience.add.silent",
                        Placeholder.parsed("experience", amount.toString())
                    ))
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
        }
    }

    @Command("levels set <player> <amount> points")
    @Permission("magenta.levels.set.experience")
    @CommandDescription("This command set to other player points")
    fun onLevelPointsSet(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "amount") amount: Int
    ) {
        magenta.virtualLevel.getUserByUUID(target.uniqueId).thenApply {
            magenta.virtualLevel.setExperience(target.uniqueId, amount)
            target.player?.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.experience.set",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("experience", amount.toString()),
                    )))
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.levels.success.experience.set.to",
                    TagResolver.resolver(
                        Placeholder.parsed("player", target.name.toString()),
                        Placeholder.parsed("experience", amount.toString()),
                    )))
        }.exceptionally {
            commandSender.sendMessage(
                magenta.locale.translation("magenta.command.level.error.not.account",
                    Placeholder.parsed("player", target.name.toString())
                ))
        }
    }

}