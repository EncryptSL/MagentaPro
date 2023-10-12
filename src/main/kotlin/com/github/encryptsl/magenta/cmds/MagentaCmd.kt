package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.command.CommandSender

@Suppress("UnstableApiUsage", "UNUSED")
@CommandMethod("magenta|mg")
@CommandDescription("Provided by plugin MagentaPro")
class MagentaCmd(private val magenta: Magenta) {

    @CommandPermission("magenta.plugin.help")
    @CommandMethod("help")
    fun onHelp(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText("<blue>―――――"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Plugin: <yellow>${magenta.pluginMeta.displayName}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Version: <color:#4C62A5>${magenta.pluginMeta.version}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Author: <color:#B4C5FB>${magenta.pluginMeta.authors}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Website: <color:#89A4F7>${magenta.pluginMeta.website}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>―――――"))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload")
    fun onReload(commandSender: CommandSender) {
        magenta.reloadConfig()
        magenta.saveConfig()
        magenta.shopConfig.reload()
        magenta.shopConfig.save()
        magenta.tags.reload()
        magenta.tags.save()
        magenta.cItems.reload()
        magenta.cItems.save()
        magenta.localeConfig.loadLocale("locale/cs_cz.properties")
        magenta.kitConfig.reload()
        magenta.kitConfig.save()
        magenta.jailConfig.reload()
        magenta.jailConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }
}