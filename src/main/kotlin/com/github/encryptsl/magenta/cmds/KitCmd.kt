package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.kit.*
import com.github.encryptsl.magenta.api.manager.KitManager
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class KitCmd(private val magenta: Magenta) {

    @CommandMethod("kit <kit>")
    @CommandPermission("magenta.kit")
    fun onKit(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        if (!player.hasPermission("magenta.kits.$kit") && !player.hasPermission("magenta.kits.*"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.not.permission")))

        magenta.schedulerMagenta.doSync(magenta) {
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
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(KitGiveEvent(commandSender, target, kit, KitManager(magenta)))
        }
    }

    @CommandMethod("kits")
    @CommandPermission("magenta.kit.list")
    fun onKitList(commandSender: CommandSender) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(KitInfoEvent(commandSender, null, InfoType.LIST))
        }
    }

    @CommandMethod("showkit <kit>")
    @CommandPermission("magenta.showkit")
    fun onShowKit(commandSender: CommandSender, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(KitInfoEvent(commandSender, kit, InfoType.INFO))
        }
    }

    @CommandMethod("createkit <kit> [delay]")
    @CommandPermission("magenta.createkit")
    fun onKitCreate(player: Player, @Argument(value = "kit") kit: String, @Argument(value = "delay", defaultValue = "150") delay: Int) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(KitCreateEvent(player, kit, delay, KitManager(magenta)))
        }
    }

    @CommandMethod("deletekit <kit>")
    @CommandPermission("magenta.deletekit")
    fun onKitDelete(commandSender: CommandSender, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.schedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(KitDeleteEvent(commandSender, kit))
        }
    }



}