package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.kit.*
import com.github.encryptsl.magenta.api.manager.KitManager
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class KitCmd(private val magenta: Magenta) {

    @Command("kit <kit>")
    @Permission("magenta.kit")
    fun onKit(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        if (!player.hasPermission("magenta.kits.$kit") && !player.hasPermission("magenta.kits.*"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.kit.error.not.permission")))

        magenta.pluginManager.callEvent(
            KitReceiveEvent(player, kit, magenta.kitConfig.getKit().getLong("kits.$kit.delay"), KitManager(magenta))
        )
    }

    @Command("kit <kit> <target>")
    @Permission("magenta.kit.other")
    fun onKitOther(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitGiveEvent(commandSender, target, kit, KitManager(magenta)))
    }

    @Command("kits")
    @Permission("magenta.kit.list")
    fun onKitList(commandSender: CommandSender) {
        magenta.pluginManager.callEvent(KitInfoEvent(commandSender, null, InfoType.LIST))
    }

    @Command("showkit <kit>")
    @Permission("magenta.showkit")
    fun onShowKit(commandSender: CommandSender, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitInfoEvent(commandSender, kit, InfoType.INFO))
    }

    @Command("createkit <kit> [delay]")
    @Permission("magenta.createkit")
    fun onKitCreate(player: Player, @Argument(value = "kit") kit: String, @Argument(value = "delay") delay: Int = 150) {
        magenta.pluginManager.callEvent(KitCreateEvent(player, kit, delay, KitManager(magenta)))
    }

    @Command("deletekit <kit>")
    @Permission("magenta.deletekit")
    fun onKitDelete(commandSender: CommandSender, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitDeleteEvent(commandSender, kit))
    }



}