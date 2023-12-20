package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.clan.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ClanListeners(private val magenta: Magenta) : Listener {
    @EventHandler
    fun onCreateClan(event: ClanCreateEvent) {} //TODO: Need deep resources

    @EventHandler
    fun onDeleteClan(event: ClanDeleteEvent) {} //TODO: Need deep resources

    @EventHandler
    fun onAddMemberToClan(event: ClanAddMemberEvent) {} //TODO: Need deep resources

    @EventHandler
    fun onChangeRoleInClan(event: ClanChangeRoleEvent) {} //TODO: Need deep resources

    @EventHandler
    fun onRenameClan(event: ClanRenameEvent) {} //TODO: Need deep resources
}