package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.clan.ClanRoles
import com.github.encryptsl.magenta.api.events.clan.*
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ClanListeners(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onCreateClan(event: ClanCreateEvent) {
        val player = event.player
        val clanName = event.clanName

        if (magenta.clanAPI.clanExist(clanName))
            return player.sendMessage(ModernText.miniModernText(""))

        magenta.clanAPI.createClan(player, clanName)
        player.sendMessage(ModernText.miniModernText(""))
    }

    @EventHandler
    fun onDeleteClan(event: ClanDeleteEvent) {
        val commandSender = event.commandSender
        val clanName = event.clanName

        if (!magenta.clanAPI.clanExist(clanName))
            return commandSender.sendMessage(ModernText.miniModernText(""))

        if (commandSender.hasPermission("magenta.clan.delete.other")) {
            magenta.clanAPI.deleteClan(clanName)
        } else {
            if (magenta.clanAPI.getClan(clanName).members[commandSender.name]?.equals(ClanRoles.OWNER.name, ignoreCase = true) == true)
                return

            magenta.clanAPI.deleteClan(clanName)
        }

        commandSender.sendMessage("")
    }

    @EventHandler
    fun onAddMemberToClan(event: ClanAddMemberEvent) {}

    @EventHandler
    fun onChangeRoleInClan(event: ClanChangeRoleEvent) {}

    @EventHandler
    fun onRenameClan(event: ClanRenameEvent) {}

}