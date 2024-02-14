package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByAdminEvent
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByPlayerEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provived by plugin MagentaPro")
class SocialSpyCmd(private val magenta: Magenta) {

    @Command("socialspy|spy")
    @Permission("magenta.social.spy")
    fun onToggleSocialSpy(player: Player) {
        magenta.pluginManager.callEvent(SpyToggleByPlayerEvent(player))
    }

    @Command("socialspy|spy <player>")
    @Permission("magenta.social.spy.other")
    fun onToggleSocialSpyOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        magenta.pluginManager.callEvent(SpyToggleByAdminEvent(commandSender, target))
    }

}