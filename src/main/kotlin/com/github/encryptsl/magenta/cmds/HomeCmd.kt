package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.home.*
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class HomeCmd(private val magenta: Magenta) {

    @CommandMethod("home <home>")
    @CommandPermission("magenta.home")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home, magenta.config.getLong("teleport-cooldown")))
        }
    }

    @CommandMethod("sethome <home>")
    @CommandPermission("magenta.sethome")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeCreateEvent(player, player.location, home))
        }

    }

    @CommandMethod("delhome <home>")
    @CommandPermission("magenta.delhome")
    fun onDeleteHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeDeleteEvent(player, home))
        }

    }

    @CommandMethod("renamehome <oldName> <newName>")
    @CommandPermission("magenta.rename.home")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeRenameEvent(player, oldName, newName))
        }
    }

    @CommandMethod("homes|homelist")
    @CommandPermission("magenta.home.list")
    fun onHomeList(player: Player) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(HomeInfoEvent(player, null, InfoType.LIST))
        }
    }
}