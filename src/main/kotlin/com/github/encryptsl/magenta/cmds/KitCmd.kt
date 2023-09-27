package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.KitManager
import com.github.encryptsl.magenta.api.events.kit.KitAdminGiveEvent
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
        if (player.hasPermission("magenta.kit.$kit"))
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
            magenta.pluginManager.callEvent(KitAdminGiveEvent(commandSender, target, kit, KitManager(magenta)))
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

    @CommandMethod("createkit <kit>")
    @CommandPermission("magenta.createkit")
    fun onKitCreate(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }

    @CommandMethod("deletekit <kit>")
    @CommandPermission("magenta.deletekit")
    fun onKitDelete(commandSender: CommandSender, player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {

    }



}