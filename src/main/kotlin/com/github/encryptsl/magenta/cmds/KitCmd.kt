package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.KitManager
import com.github.encryptsl.magenta.api.events.kit.KitAdminGiveEvent
import com.github.encryptsl.magenta.api.events.kit.KitReceiveEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class KitCmd(private val magenta: Magenta) {

    @CommandMethod("kit <kit>")
    @CommandPermission("magenta.kit")
    fun onKit(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitReceiveEvent(player, kit, magenta.kitConfig.getKit().getLong("kits.$kit.delay"), KitManager(magenta)))
    }

    @CommandMethod("kit <player> <kit>")
    @CommandPermission("magenta.kit.other")
    fun onKitOther(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitAdminGiveEvent(commandSender, target, kit, KitManager(magenta)))
    }

    @CommandMethod("createkit <kit>")
    @CommandPermission("magenta.createkit")
    fun onKitCreate(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }

    @CommandMethod("deletekit <kit>")
    @CommandPermission("magenta.deletekit")
    fun onKitDelete(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }



}