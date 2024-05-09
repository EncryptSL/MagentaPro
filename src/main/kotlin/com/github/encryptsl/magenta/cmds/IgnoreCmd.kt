package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class IgnoreCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("ignoredPlayers") {sender, _ ->
            val c = sender as Player
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.user.getUser(c.uniqueId).getAccount().getStringList("ignore").map { Suggestion.suggestion(Bukkit.getOfflinePlayer(it).name.toString()) }
            )
        }
        annotationParser.parse(this)
    }

    @Command("ignore <player>")
    @Permission("magenta.ignore")
    @CommandDescription("This command start ignore player in chat and pm")
    fun onIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        if (player.uniqueId == target.uniqueId)
            return player.sendMessage(magenta.locale.translation("magenta.command.ignore.error.yourself"))

        val user = magenta.user.getUser(player.uniqueId)
        if (user.isPlayerIgnored(target.uniqueId))
            return player.sendMessage(magenta.locale.translation("magenta.command.ignore.error.exist",
                Placeholder.parsed("player", target.name.toString())
            ))


        if (magenta.stringUtils.inInList("exempt-blacklist", target.name.toString()))
            return player.sendMessage(magenta.locale.translation("magenta.command.ignore.error.exempt",
                Placeholder.parsed("player", target.name.toString())
            ))

        if (luckPermsAPI.hasPermission(target, Permissions.IGNORE_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.ignore.error.exempt",
                Placeholder.parsed("player", target.name.toString())
            ))

        user.addToIgnore(target.uniqueId)
        player.sendMessage(magenta.locale.translation("magenta.command.ignore.success",
            Placeholder.parsed("player", target.name.toString())
        ))
    }

    @Command("unignore <player>")
    @Permission("magenta.unignore")
    @CommandDescription("This command remove player from your ignored list")
    fun onUnIgnore(player: Player, @Argument(value = "player", suggestions = "ignoredPlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(player.uniqueId)

        if (!user.isPlayerIgnored(target.uniqueId))
            return player.sendMessage(magenta.locale.translation("magenta.command.ignore.error.not.exist",
                Placeholder.parsed("player", target.name.toString()))
            )

        user.removeIgnoredPlayer(target.uniqueId)
        player.sendMessage(magenta.locale.translation("magenta.command.ignore.success.removed",
            Placeholder.parsed("player", target.name.toString())
        ))
    }

}