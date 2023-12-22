package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException

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
    @CommandMethod("reload config")
    fun onReloadConfig(commandSender: CommandSender) {
        try {
            magenta.config.options().parseComments(true)
            magenta.reloadConfig()
            magenta.saveConfig()
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
        } catch (e : InvalidConfigurationException) {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"), Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload shop")
    fun onReloadShopConfig(commandSender: CommandSender) {
        magenta.shopConfig.reload()
        magenta.shopConfig.save()
        magenta.creditShopConfig.reload()
        magenta.creditShopConfig.save()
        ShopHelper.reloadShopConfigs(magenta)
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload mythicrewards")
    fun onReloadMythicRewards(commandSender: CommandSender) {
        magenta.mmConfig.reload()
        magenta.mmConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload randomconfig")
    fun onReloadRandomConfig(commandSender: CommandSender) {
        magenta.randomConfig.reload()
        magenta.randomConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload citems")
    fun onReloadActivationItems(commandSender: CommandSender) {
        magenta.cItems.reload()
        magenta.cItems.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload locale")
    fun onReloadLocale(commandSender: CommandSender) {
        magenta.localeConfig.loadLocale("locale/cs_cz.properties")
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload kits")
    fun onReloadKits(commandSender: CommandSender) {
        magenta.kitConfig.reload()
        magenta.kitConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload jails")
    fun onReloadJails(commandSender: CommandSender) {
        magenta.jailConfig.reload()
        magenta.jailConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @CommandPermission("magenta.plugin.reload")
    @CommandMethod("reload chatcontrol")
    fun onReloadChatControl(commandSender: CommandSender) {
        magenta.chatControl.reload()
        magenta.chatControl.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }




}