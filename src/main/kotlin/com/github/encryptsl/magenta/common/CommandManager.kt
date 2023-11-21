package com.github.encryptsl.magenta.common

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.paper.PaperCommandManager
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
import java.util.function.Function


class CommandManager(private val magenta: Magenta) {

    private fun createCommandManager(): PaperCommandManager<CommandSender> {
        val executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.builder<CommandSender>().build()
        val mapperFunction = Function.identity<CommandSender>()
        val commandManager = PaperCommandManager(
            magenta,
            executionCoordinatorFunction,
            mapperFunction,
            mapperFunction
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager()?.setNativeNumberSuggestions(false)
        }
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as PaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }


    private fun createAnnotationParser(commandManager: PaperCommandManager<CommandSender>): AnnotationParser<CommandSender> {
        val commandMetaFunction = Function<ParserParameters, CommandMeta> { p: ParserParameters ->
            CommandMeta.simple() // Decorate commands with descriptions
                .with(CommandMeta.DESCRIPTION, p[StandardParameters.DESCRIPTION, "No Description"])
                .build()
        }
        return AnnotationParser(
            commandManager,
            CommandSender::class.java,
            commandMetaFunction /* Mapper for command meta instances */
        )
    }

    private fun registerSuggestionProviders(commandManager: PaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("gamemodes") {sender, _ ->
            GameMode.entries.filter { sender.hasPermission("magenta.gamemodes.${it.name.lowercase()}") }.map { it.name.lowercase() }
        }
        commandManager.parserRegistry().registerSuggestionProvider("players") {_, input ->
            Bukkit.getOnlinePlayers().toList().filter { p -> p.name.startsWith(input, ignoreCase = true) }.mapNotNull { it.name }
        }
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, input ->
            Bukkit.getOfflinePlayers().toList()
                .filter { p ->
                    p.name?.startsWith(input, ignoreCase = true) ?: false
                }
                .mapNotNull { it.name }
        }
        commandManager.parserRegistry().registerSuggestionProvider("citems") {_, _ ->
            magenta.cItems.getConfig().getConfigurationSection("citems")
                ?.getKeys(false)?.mapNotNull { a -> a.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("kits") { commandSender, _ ->
            magenta.kitConfig.getKit().getConfigurationSection("kits")?.getKeys(false)
                ?.filter { kit -> commandSender.hasPermission("magenta.kits.$kit") }
                ?.mapNotNull { a -> a.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("jails") { _, _ ->
            magenta.jailConfig.getJail().getConfigurationSection("jails")
                ?.getKeys(false)
                ?.mapNotNull { it.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("homes") { sender, _ ->
            val player = sender.sender as Player
            return@registerSuggestionProvider magenta.homeModel.getHomesByOwner(player).map { s -> s.homeName }
        }
        commandManager.parserRegistry().registerSuggestionProvider("warps") {_, _ ->
            magenta.warpModel.getWarps().map { s -> s.warpName }
        }
        commandManager.parserRegistry().registerSuggestionProvider("tags") {_, _ ->
            magenta.randomConfig.getConfig().getConfigurationSection("tags")
                ?.getKeys(false)
                ?.mapNotNull { it.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("materials") {_, _ ->
            materials()
        }
        commandManager.parserRegistry().registerSuggestionProvider("mobs") {_, _ ->
            entities()
        }
        commandManager.parserRegistry().registerSuggestionProvider("shops") {_, _ ->
            magenta.shopConfig.getConfig().getConfigurationSection("shop.categories")
                ?.getKeys(false)
                ?.mapNotNull { it.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("creditshops") {_, _ ->
            magenta.creditShopConfig.getConfig().getConfigurationSection("shop.categories")
                ?.getKeys(false)
                ?.mapNotNull { it.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("services") {_, _ ->
            magenta.config.getConfigurationSection("votifier.services")
                ?.getKeys(false)
                ?.mapNotNull { VoteHelper.replaceService(it.toString(), "_", ".") } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("reportCategories") {_, _ ->
            ReportCategories.entries.map { it.name }
        }
        commandManager.parserRegistry().registerSuggestionProvider("worlds") {_, _ ->
            Bukkit.getWorlds().map { it.name }
        }
    }

    private fun materials(): List<String> {
        return Material.entries.map { it.name }
    }

    private fun entities(): List<String> {
        return EntityType.entries.map { it.name }
    }

    fun registerCommands() {
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
    }

}