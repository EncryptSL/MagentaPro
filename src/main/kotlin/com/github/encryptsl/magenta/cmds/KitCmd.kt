package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.KitManager
import com.github.encryptsl.magenta.api.events.kit.KitGiveEvent
import com.github.encryptsl.magenta.api.events.kit.KitCreateEvent
import com.github.encryptsl.magenta.api.events.kit.KitInfoEvent
import com.github.encryptsl.magenta.api.events.kit.KitReceiveEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class KitCmd(private val magenta: Magenta) {

    @CommandMethod("kit <kit>")
    @CommandPermission("magenta.kit")
    fun onKit(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        if (!player.hasPermission("magenta.kit.$kit"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.not.permission")))

        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(
                KitReceiveEvent(
                    player,
                    kit,
                    magenta.kitConfig.getKit().getLong("kits.$kit.delay"),
                    KitManager(magenta)
                )
            )
        }
    }

    @CommandMethod("kit <kit> <target>")
    @CommandPermission("magenta.kit.other")
    fun onKitOther(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(KitGiveEvent(commandSender, target, kit, KitManager(magenta)))
        }
    }

    @ProxiedBy("kits")
    @CommandMethod("kit list")
    @CommandPermission("magenta.kit.list")
    fun onKitList(commandSender: CommandSender) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(KitInfoEvent(commandSender, null, InfoType.LIST))
        }
    }

    @CommandMethod("createkit <kit> [delay]")
    @CommandPermission("magenta.createkit")
    fun onKitCreate(player: Player, @Argument(value = "kit") kit: String, @Argument(value = "delay", defaultValue = "150") delay: Int) {
        magenta.schedulerMagenta.runTask(magenta) {
            magenta.pluginManager.callEvent(KitCreateEvent(player, kit, delay, KitManager(magenta)))
        }
    }

    @CommandMethod("deletekit <kit>")
    @CommandPermission("magenta.deletekit")
    fun onKitDelete(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }



}