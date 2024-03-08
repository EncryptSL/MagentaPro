package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UnstableApiUsage", "UNUSED")
@Command("magenta|mg")
@CommandDescription("Provided by plugin MagentaPro")
class MagentaCmd(private val magenta: Magenta) {

    @Permission("magenta.plugin.help")
    @Command("help")
    fun onHelp(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText("<blue>―――――"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Plugin: <yellow>${magenta.pluginMeta.displayName}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Version: <color:#4C62A5>${magenta.pluginMeta.version}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Author: <color:#B4C5FB>${magenta.pluginMeta.authors}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>| <color:#4C76FB>Website: <color:#89A4F7>${magenta.pluginMeta.website}"))
        commandSender.sendMessage(ModernText.miniModernText("<blue>―――――"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload config")
    fun onReloadConfig(commandSender: CommandSender) {
        try {
            magenta.config.options().parseComments(true)
            magenta.reloadConfig()
            magenta.saveConfig()
            magenta.newsQueueManager.reloadQueue()
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
        } catch (e : InvalidConfigurationException) {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.exception"), Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    @Permission("magenta.plugin.reload")
    @Command("reload shop")
    fun onReloadShopConfig(commandSender: CommandSender) {
        magenta.shopConfig.reload()
        magenta.shopConfig.save()
        magenta.creditShopConfig.reload()
        magenta.creditShopConfig.save()
        magenta.creditShopConfirmMenuConfig.reload()
        magenta.creditShopConfirmMenuConfig.save()
        ShopHelper.reloadShopConfigs(magenta)
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload homegui")
    fun onReloadHomeGUI(commandSender: CommandSender) {
        magenta.homeMenuConfig.reload()
        magenta.homeMenuConfig.save()
        magenta.homeEditorConfig.reload()
        magenta.homeEditorConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload warpgui")
    fun onReloadWarpGUI(commandSender: CommandSender) {
        magenta.warpMenuConfig.reload()
        magenta.warpMenuConfig.save()
        magenta.warpEditorConfig.reload()
        magenta.warpEditorConfig.save()
        magenta.warpPlayerMenuConfig.reload()
        magenta.warpPlayerMenuConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload oregui")
    fun onReloadLevelOreGUI(commandSender: CommandSender) {
        magenta.oresMenuConfig.reload()
        magenta.oresMenuConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload mythicrewards")
    fun onReloadMythicRewards(commandSender: CommandSender) {
        magenta.mmConfig.reload()
        magenta.mmConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload randomconfig")
    fun onReloadRandomConfig(commandSender: CommandSender) {
        magenta.randomConfig.reload()
        magenta.randomConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload citems")
    fun onReloadActivationItems(commandSender: CommandSender) {
        magenta.cItems.reload()
        magenta.cItems.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload locale")
    fun onReloadLocale(commandSender: CommandSender) {
        magenta.localeConfig.loadLocale("locale/cs_cz.properties")
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload kits")
    fun onReloadKits(commandSender: CommandSender) {
        magenta.kitConfig.reload()
        magenta.kitConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload jails")
    fun onReloadJails(commandSender: CommandSender) {
        magenta.jailConfig.reload()
        magenta.jailConfig.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload chatcontrol")
    fun onReloadChatControl(commandSender: CommandSender) {
        magenta.chatControl.reload()
        magenta.chatControl.save()
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.magenta.success.reload")))
    }




}