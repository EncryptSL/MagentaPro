package com.github.encryptsl.magenta.common

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.paper.PaperCommandManager
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.cmds.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
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
        commandManager.parserRegistry().registerSuggestionProvider("kits") { commandSender, input ->
            magenta.kitConfig.getKit().getConfigurationSection("kits")?.getKeys(false)
                ?.filter { kit -> commandSender.hasPermission("magenta.kits.$kit") }
                ?.mapNotNull { a -> a.toString() } ?: emptyList()
        }
        commandManager.parserRegistry().registerSuggestionProvider("jails") { _, input ->
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
    }

    fun registerCommands() {
        magenta.logger.info("Registering commands with Cloud Command Framework !")

        val commandManager = createCommandManager()

        registerSuggestionProviders(commandManager)

        val annotationParser = createAnnotationParser(commandManager)
        annotationParser.parse(AfkCmd(magenta))
        annotationParser.parse(BackCmd(magenta))
        annotationParser.parse(FlyCmd(magenta))
        annotationParser.parse(GmCmd(magenta))
        annotationParser.parse(HealCmd(magenta))
        annotationParser.parse(HomeCmd(magenta))
        annotationParser.parse(JailCmd(magenta))
        annotationParser.parse(KitCmd(magenta))
        annotationParser.parse(LightningCmd(magenta))
        annotationParser.parse(MagentaCmd(magenta))
        annotationParser.parse(NickCmd(magenta))
        annotationParser.parse(IgnoreCmd(magenta))
        annotationParser.parse(MsgCmd(magenta))
        annotationParser.parse(RepairCmd(magenta))
        annotationParser.parse(SocialSpyCmd(magenta))
        annotationParser.parse(TpCmd(magenta))
        annotationParser.parse(VanishCmd(magenta))
        annotationParser.parse(WarpCmd(magenta))
    }

}