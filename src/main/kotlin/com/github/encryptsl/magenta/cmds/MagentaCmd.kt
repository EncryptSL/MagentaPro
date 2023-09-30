package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender

@CommandMethod("magenta|mg")
@CommandDescription("Provided by plugin MagentaPro")
class MagentaCmd(private val magenta: Magenta) {

    @CommandPermission("magenta.plugin.help")
    @CommandMethod("help")
    fun onHelp(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText("============="))
        commandSender.sendMessage(ModernText.miniModernText("= Plugin: ${magenta.pluginMeta.displayName}"))
        commandSender.sendMessage(ModernText.miniModernText("= Version: ${magenta.pluginMeta.version}"))
        commandSender.sendMessage(ModernText.miniModernText("= Author: ${magenta.pluginMeta.authors}"))
        commandSender.sendMessage(ModernText.miniModernText("= Website: ${magenta.pluginMeta.website}"))
        commandSender.sendMessage(ModernText.miniModernText("============="))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload")
    fun onReload(commandSender: CommandSender) {
        magenta.reloadConfig()
        magenta.saveConfig()
        magenta.localeConfig.loadLocale("locale/cs_cz.properties")
        magenta.kitConfig.reload()
        magenta.kitConfig.save()
        magenta.jailConfig.reload()
        magenta.jailConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }
}