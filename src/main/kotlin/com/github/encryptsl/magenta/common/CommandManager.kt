package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.report.ReportCategories
import com.github.encryptsl.magenta.cmds.*
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture


class CommandManager(private val magenta: Magenta) {

    private fun createCommandManager(): PaperCommandManager<CommandSender> {
        val executionCoordinatorFunction = ExecutionCoordinator.builder<CommandSender>().build()
        val mapperFunction = SenderMapper.identity<CommandSender>()
        val commandManager = PaperCommandManager(
            magenta,
            executionCoordinatorFunction,
            mapperFunction,
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager().setNativeNumberSuggestions(false)
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as PaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }


    private fun createAnnotationParser(commandManager: PaperCommandManager<CommandSender>): AnnotationParser<CommandSender> {
        return AnnotationParser(
            commandManager,
            CommandSender::class.java,
        )
    }

    private fun registerSuggestionProviders(commandManager: PaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("gamemodes") {sender, _ ->
            CompletableFuture.completedFuture(
                GameMode.entries
                    .filter { sender.hasPermission("magenta.gamemodes.${it.name.lowercase()}") }
                    .map { Suggestion.simple(it.name) }
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("players") {_, input ->
            CompletableFuture.completedFuture(Bukkit.getOnlinePlayers()
                .filter { p -> p.name.startsWith(input.input(), true) }
                .map { Suggestion.simple(it.name) }
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, _ ->
            CompletableFuture.completedFuture(Bukkit.getOfflinePlayers()
                .map { Suggestion.simple(it.name.toString()) }
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("citems") {_, _ ->
            CompletableFuture.completedFuture(magenta.cItems.getConfig().getConfigurationSection("citems")
                ?.getKeys(false)
                ?.mapNotNull { a -> Suggestion.simple(a.toString()) }!!)
        }
        commandManager.parserRegistry().registerSuggestionProvider("kits") { commandSender, _ ->
            CompletableFuture.completedFuture(
                magenta.kitConfig.getKit().getConfigurationSection("kits")?.getKeys(false)
                    ?.filter { kit -> commandSender.hasPermission("magenta.kits.$kit") }
                    ?.mapNotNull { a -> Suggestion.simple(a.toString()) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("jails") { _, _ ->
            CompletableFuture.completedFuture(
                magenta.jailConfig.getJail().getConfigurationSection("jails")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.simple(it.toString()) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("homes") { sender, _ ->
            val player = sender.sender() as Player
            return@registerSuggestionProvider CompletableFuture.completedFuture(magenta.homeModel.getHomesByOwner(player).map { s -> Suggestion.simple(s.homeName) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("warps") {_, _ ->
            CompletableFuture.completedFuture(magenta.warpModel.getWarps().map { s -> Suggestion.simple(s.warpName) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("tags") {_, _ ->
            CompletableFuture.completedFuture(
                magenta.randomConfig.getConfig().getConfigurationSection("tags")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.simple(it.toString()) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("materials") {_, _ ->
            CompletableFuture.completedFuture(Material.entries.map { Suggestion.simple(it.name) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("mobs") {_, _ ->
            CompletableFuture.completedFuture(EntityType.entries.map { Suggestion.simple(it.name) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("shops") {_, _ ->
            CompletableFuture.completedFuture(
                magenta.shopConfig.getConfig().getConfigurationSection("shop.categories")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.simple(it.toString()) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("creditshops") {_, _ ->
            CompletableFuture.completedFuture(
                magenta.creditShopConfig.getConfig().getConfigurationSection("shop.categories")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.simple(it.toString()) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("services") {_, _ ->
            CompletableFuture.completedFuture(
                magenta.config.getConfigurationSection("votifier.services")
                    ?.getKeys(false)
                    ?.mapNotNull { Suggestion.simple(VoteHelper.replaceService(it.toString(), "_", ".")) }!!
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("reportCategories") {_, _ ->
            CompletableFuture.completedFuture(ReportCategories.entries.map { Suggestion.simple(it.name) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("worlds") {_, _ ->
            CompletableFuture.completedFuture(Bukkit.getWorlds().map { Suggestion.simple(it.name) })
        }
    }

    fun registerCommands() {
        try {
            magenta.logger.info("Registering commands with Cloud Command Framework !")
            val commandManager = createCommandManager()
            registerSuggestionProviders(commandManager)
            val annotationParser = createAnnotationParser(commandManager)

            annotationParser.parse(AfkCmd(magenta))
            annotationParser.parse(BackCmd(magenta))
            annotationParser.parse(BroadcastCmd(magenta))
            annotationParser.parse(CommandItemsCmd(magenta))
            annotationParser.parse(ContainersCmd(magenta))
            annotationParser.parse(EnderChestCmd(magenta))
            annotationParser.parse(FeedbackCmd(magenta))
            annotationParser.parse(FlyCmd(magenta))
            annotationParser.parse(GmCmd(magenta))
            annotationParser.parse(HatCmd(magenta))
            annotationParser.parse(HealCmd(magenta))
            annotationParser.parse(HomeCmd(magenta))
            annotationParser.parse(IgnoreCmd(magenta))
            annotationParser.parse(InvseeCmd(magenta))
            annotationParser.parse(JailCmd(magenta))
            annotationParser.parse(KitCmd(magenta))
            annotationParser.parse(LevelCmd(magenta))
            annotationParser.parse(LevelsCmd(magenta))
            annotationParser.parse(LightningCmd(magenta))
            annotationParser.parse(MagentaCmd(magenta))
            annotationParser.parse(NickCmd(magenta))
            annotationParser.parse(OresCmd(magenta))
            annotationParser.parse(RandomCmd(magenta))
            annotationParser.parse(MsgCmd(magenta))
            annotationParser.parse(RepairCmd(magenta))
            annotationParser.parse(ReportCmd(magenta))
            annotationParser.parse(ShopCmd(magenta))
            annotationParser.parse(SocialSpyCmd(magenta))
            annotationParser.parse(SpawnerCmd(magenta))
            annotationParser.parse(TpCmd(magenta))
            annotationParser.parse(VanishCmd(magenta))
            annotationParser.parse(VoteCmd(magenta))
            annotationParser.parse(VotePartyCmd(magenta))
            annotationParser.parse(VotesCmd(magenta))
            annotationParser.parse(WarpCmd(magenta))
        } catch (e : NoClassDefFoundError) {
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

}