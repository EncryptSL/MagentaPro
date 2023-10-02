package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.home.*
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
@CommandMethod("home")
class HomeCmd(private val magenta: Magenta) {

    @CommandMethod("info <home>")
    @CommandPermission("magenta.home.info")
    fun onRenameHome(player: Player, @Argument(value = "home", suggestions = "homes") homeName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(HomeInfoEvent(player, homeName, InfoType.INFO))
        }
    }

    @CommandMethod("tp <home>")
    @CommandPermission("magenta.home.tp")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home))
        }
    }

    @ProxiedBy("sethome")
    @CommandMethod("create <home>")
    @CommandPermission("magenta.home.create")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(HomeCreateEvent(player, player.location, home))
        }

    }

    @ProxiedBy("delhome")
    @CommandMethod("delete <home>")
    @CommandPermission("magenta.home.delete")
    fun onDeleteHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(HomeDeleteEvent(player, home))
        }

    }

    @CommandMethod("rename <oldName> <newName>")
    @CommandPermission("magenta.home.rename")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.server.pluginManager.callEvent(HomeRenameEvent(player, oldName, newName))
        }
    }


    @ProxiedBy("homes")
    @CommandMethod("homes")
    @CommandPermission("magenta.home.list")
    fun onHomeList(player: Player) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(HomeInfoEvent(player, null, InfoType.LIST))
        }
    }
}