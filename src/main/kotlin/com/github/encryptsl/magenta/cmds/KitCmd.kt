package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.events.kit.*
import com.github.encryptsl.magenta.common.model.KitManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class KitCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("kits") { commandSender, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                magenta.kitConfig.getConfig().getConfigurationSection("kits")?.getKeys(false)
                    ?.filter { kit -> commandSender.hasPermission("magenta.kits.$kit") }
                    ?.mapNotNull { a -> Suggestion.simple(a.toString()) }!!
            )
        }
        annotationParser.parse(this)
    }

    @Command("kit <kit>")
    @Permission("magenta.kit")
    @CommandDescription("This command give you kit")
    fun onKit(player: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        if (!player.hasPermission("magenta.kits.$kit") && !player.hasPermission("magenta.kits.*"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.kit.error.not.permission"))

        magenta.pluginManager.callEvent(
            KitReceiveEvent(player, kit, magenta.kitConfig.getConfig().getLong("kits.$kit.delay"), KitManager(magenta))
        )
    }

    @Command("kit <kit> <target>")
    @Permission("magenta.kit.other")
    @CommandDescription("This command give kit to other player")
    fun onKitOther(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitGiveEvent(commandSender, target, kit, KitManager(magenta)))
    }

    @Command("kits")
    @Permission("magenta.kit.list")
    @CommandDescription("This command send kits list")
    fun onKitList(commandSender: CommandSender) {
        magenta.pluginManager.callEvent(KitInfoEvent(commandSender, null, InfoType.LIST))
    }

    @Command("showkit <kit>")
    @Permission("magenta.showkit")
    @CommandDescription("This command send information about kit")
    fun onShowKit(commandSender: CommandSender, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitInfoEvent(commandSender, kit, InfoType.INFO))
    }

    @Command("createkit <kit> [delay]")
    @Permission("magenta.createkit")
    @CommandDescription("This command create kit from your inventory")
    fun onKitCreate(player: Player, @Argument(value = "kit") kit: String, @Argument(value = "delay") delay: Int = 150) {
        magenta.pluginManager.callEvent(KitCreateEvent(player, kit, delay, KitManager(magenta)))
    }

    @Command("deletekit|delkit <kit>")
    @Permission("magenta.deletekit")
    @CommandDescription("This command delete kit")
    fun onKitDelete(commandSender: CommandSender, @Argument(value = "kit", suggestions = "kits") kit: String) {
        magenta.pluginManager.callEvent(KitDeleteEvent(commandSender, kit))
    }



}