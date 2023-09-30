package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByAdminEvent
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByPlayerEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provived by plugin MagentaPro")
class SocialSpyCmd(private val magenta: Magenta) {

    @CommandMethod("socialspy|spy")
    @CommandPermission("magenta.social.spy")
    fun onToggleSocialSpy(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(SpyToggleByPlayerEvent(player))
        }
    }

    @CommandMethod("socialspy|spy <player>")
    @CommandPermission("magenta.social.spy.other")
    fun onToggleSocialSpyOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(SpyToggleByAdminEvent(commandSender, target))
        }
    }

}