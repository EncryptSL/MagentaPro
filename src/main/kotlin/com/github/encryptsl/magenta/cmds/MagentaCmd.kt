package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopHelper
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
@Command("magenta|mg")
class MagentaCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Permission("magenta.plugin.help")
    @Command("help [query]")
    fun onHelp(
        commandSender: CommandSender,
        @Argument(value = "query", suggestions = "help_queries") @Greedy query: String?
    ) {

        val getQuery = query ?: ""

        magenta.commandManager.help?.queryCommands(getQuery, commandSender)
    }

    @Permission("magenta.plugin.reload")
    @Command("reload config")
    fun onReloadConfig(commandSender: CommandSender) {
        try {
            magenta.config.options().parseComments(true)
            magenta.reloadConfig()
            magenta.saveConfig()
            magenta.newsQueueManager.reloadQueue()
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
        } catch (e : InvalidConfigurationException) {
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage)))
        }
    }

    @Permission("magenta.plugin.reload")
    @Command("reload shop")
    @CommandDescription("This command reload shop configs")
    fun onReloadShopConfig(commandSender: CommandSender) {
        magenta.shopConfig.reload()
        magenta.shopConfig.save()
        magenta.creditShopConfig.reload()
        magenta.creditShopConfig.save()
        magenta.creditShopConfirmMenuConfig.reload()
        magenta.creditShopConfirmMenuConfig.save()
        ShopHelper.reloadShopConfigs(magenta)
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload homegui")
    @CommandDescription("This command reload home configs")
    fun onReloadHomeGUI(commandSender: CommandSender) {
        magenta.homeMenuConfig.reload()
        magenta.homeMenuConfig.save()
        magenta.homeEditorConfig.reload()
        magenta.homeEditorConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload warpgui")
    @CommandDescription("This command reload warp gui configs")
    fun onReloadWarpGUI(commandSender: CommandSender) {
        magenta.warpMenuConfig.reload()
        magenta.warpMenuConfig.save()
        magenta.warpEditorConfig.reload()
        magenta.warpEditorConfig.save()
        magenta.warpPlayerMenuConfig.reload()
        magenta.warpPlayerMenuConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload oregui")
    @CommandDescription("This command reload ores gui config")
    fun onReloadLevelOreGUI(commandSender: CommandSender) {
        magenta.oresMenuConfig.reload()
        magenta.oresMenuConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload mythicrewards")
    @CommandDescription("This command reload config with mythic rewards")
    fun onReloadMythicRewards(commandSender: CommandSender) {
        magenta.mmConfig.reload()
        magenta.mmConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload randomconfig")
    @CommandDescription("This command reload config with rewards")
    fun onReloadRandomConfig(commandSender: CommandSender) {
        magenta.randomConfig.reload()
        magenta.randomConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload citems")
    @CommandDescription("This command reload activation config")
    fun onReloadActivationItems(commandSender: CommandSender) {
        magenta.cItems.reload()
        magenta.cItems.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload locale")
    @CommandDescription("This command reload locale config")
    fun onReloadLocale(commandSender: CommandSender) {
        magenta.localeConfig.loadLocale("locale/cs_cz.properties")
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload kits")
    @CommandDescription("This command reload kit config")
    fun onReloadKits(commandSender: CommandSender) {
        magenta.kitConfig.reload()
        magenta.kitConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload jails")
    @CommandDescription("This command reload jail config")
    fun onReloadJails(commandSender: CommandSender) {
        magenta.jailConfig.reload()
        magenta.jailConfig.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }

    @Permission("magenta.plugin.reload")
    @Command("reload chatcontrol")
    @CommandDescription("This command reload chat control config")
    fun onReloadChatControl(commandSender: CommandSender) {
        magenta.chatControl.reload()
        magenta.chatControl.save()
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.magenta.success.reload"))
    }




}