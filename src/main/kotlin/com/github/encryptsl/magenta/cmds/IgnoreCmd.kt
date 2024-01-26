package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.Command
import cloud.commandframework.annotations.Permission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class IgnoreCmd(private val magenta: Magenta) {

    @Command("ignore <player>")
    @Permission("magenta.ignore")
    fun onIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        if (player.uniqueId == target.uniqueId)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.yourself")))

        val user = magenta.user.getUser(player.uniqueId)
        if (user.getAccount().getStringList("ignore").contains(target.uniqueId.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exist"),
                Placeholder.parsed("player", target.name.toString())
            ))


        if (target.player?.hasPermission("magenta.ignore.exempt") == true || magenta.stringUtils.inInList("exempt-blacklist", target.name.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exempt"),
                Placeholder.parsed("player", target.name.toString())
            ))

        user.set("ignore", setOf(target.uniqueId.toString()))
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success"),
            Placeholder.parsed("player", target.name.toString())
        ))
    }

    @Command("unignore <player>")
    @Permission("magenta.unignore")
    fun onUnIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        val user = magenta.user.getUser(player.uniqueId)

        if (!user.getAccount().getStringList("ignore").contains(target.uniqueId.toString()))
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