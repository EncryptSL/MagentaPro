package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.ignore.PlayerInsertIgnoreEvent
import com.github.encryptsl.magenta.api.events.ignore.PlayerRemoveIgnoreEvent
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class IgnoreCmd(private val magenta: Magenta) {

    @CommandMethod("ignore <player>")
    @CommandPermission("magenta.ignore")
    fun onIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(PlayerInsertIgnoreEvent(player, target))
        }
    }

    @CommandMethod("unignore <player>")
    @CommandPermission("magenta.unignore")
    fun onUnIgnore(player: Player, @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(PlayerRemoveIgnoreEvent(player, target))
        }
    }

}