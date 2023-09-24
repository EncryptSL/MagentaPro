package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeCreateEvent
import com.github.encryptsl.magenta.api.events.home.HomeDeleteEvent
import com.github.encryptsl.magenta.api.events.home.HomeRenameEvent
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
@CommandMethod("home")
class HomeCmd(private val magenta: Magenta) {

    @CommandMethod("tp <home>")
    @CommandPermission("magenta.home.tp")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home))
    }

    @CommandMethod("create <home>")
    @CommandPermission("magenta.home.create")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {
        magenta.server.pluginManager.callEvent(HomeCreateEvent(player, player.location, home))

    }

    @CommandMethod("delete <home>")
    @CommandPermission("magenta.home.delhome")
    fun onDeleteHome(player: Player, @Argument(value = "home") home: String) {
        magenta.server.pluginManager.callEvent(HomeDeleteEvent(player, home))

    }

    @CommandMethod("rename <oldName> <newName>")
    @CommandPermission("magenta.home.rename")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {
        magenta.server.pluginManager.callEvent(HomeRenameEvent(player, oldName, newName))
    }


    @ProxiedBy("homes")
    @CommandMethod("homes")
    @CommandPermission("magenta.home.list")
    fun onHomeList(commandSender: CommandSender) {
        if (commandSender is Player) {
            val list = magenta.homeModel.getHomes().filter { a -> a.uuid.equals(commandSender.uniqueId.toString(), false) }.joinToString { s -> "${s.homeName}," }
            commandSender.sendMessage(ModernText.miniModernText(list))
        } else {
            val list = magenta.homeModel.getHomes().joinToString { s -> s.homeName }
            commandSender.sendMessage(ModernText.miniModernText("<gray> $list"))
        }
    }

}