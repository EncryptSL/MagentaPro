package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class IgnoreCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("ignore <player>")
    @Permission("magenta.ignore")
    @CommandDescription("This command start ignore player in chat and pm")
    fun onIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        if (player.uniqueId == target.uniqueId)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.yourself")))

        val user = magenta.user.getUser(player.uniqueId)
        if (user.isPlayerIgnored(target.uniqueId))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exist"),
                Placeholder.parsed("player", target.name.toString())
            ))


        if (magenta.stringUtils.inInList("exempt-blacklist", target.name.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exempt"),
                Placeholder.parsed("player", target.name.toString())
            ))

        if (luckPermsAPI.hasPermission(target,"magenta.ignore.exempt"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exempt"),
                Placeholder.parsed("player", target.name.toString())
            ))

        val ignoreList:MutableList<String> = user.getAccount().getStringList("ignore")
        ignoreList.add(target.uniqueId.toString())

        user.set("ignore", ignoreList)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success"),
            Placeholder.parsed("player", target.name.toString())
        ))
    }

    @Command("unignore <player>")
    @Permission("magenta.unignore")
    @CommandDescription("This command remove player from your ignored list")
    fun onUnIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(player.uniqueId)

        if (!user.isPlayerIgnored(target.uniqueId))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.not.exist"),
                Placeholder.parsed("player", target.name.toString()))
            )

        val list: MutableList<String> = user.getAccount().getStringList("ignore")
        list.remove(target.uniqueId.toString())
        user.set("ignore", list)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success.removed"),
            Placeholder.parsed("player", target.name.toString())
        ))
    }

}