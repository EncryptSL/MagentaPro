package com.github.encryptsl.magenta.common

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.commands.AnnotationCommandRegister
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.cmds.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.minecraft.extras.AudienceProvider
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.minecraft.extras.MinecraftHelp
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture


class CommandManager(private val magenta: Magenta) {

    var help: MinecraftHelp<CommandSender>? = null

    private fun createCommandManager(): LegacyPaperCommandManager<CommandSender> {
        val commandManager = LegacyPaperCommandManager(
            magenta,
            ExecutionCoordinator.builder<CommandSender>().build(),
            SenderMapper.identity<CommandSender>(),
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager().setNativeNumberSuggestions(false)
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as LegacyPaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }


    private fun createAnnotationParser(commandManager: LegacyPaperCommandManager<CommandSender>): AnnotationParser<CommandSender> {
        return AnnotationParser<CommandSender>(commandManager, CommandSender::class.java)
    }

    private fun helpManager(commandManager: LegacyPaperCommandManager<CommandSender>) {
        this.help = MinecraftHelp.builder<CommandSender>()
                .commandManager(commandManager)
                .audienceProvider(AudienceProvider.nativeAudience())
                .commandPrefix("/magenta help")
                .colors(
                    MinecraftHelp.helpColors(
                        NamedTextColor.GREEN,
                        NamedTextColor.GOLD,
                        NamedTextColor.YELLOW,
                        NamedTextColor.GRAY,
                        NamedTextColor.WHITE
                    )
                ).build()
    }

    private fun registerMinecraftExceptionHandler(commandManager: LegacyPaperCommandManager<CommandSender>) {
        MinecraftExceptionHandler.createNative<CommandSender>()
            .defaultHandlers()
            .decorator { component ->
                ModernText.miniModernText(magenta.config.getString("prefix", "<red>[<bold>!</bold>]").toString())
                    .appendSpace()
                    .append(component)
            }
            .registerTo(commandManager)
    }

    private fun registerGlobalSuggestionProviders(commandManager: LegacyPaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("help_queries") { sender, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(
                createCommandManager().createHelpHandler().queryRootIndex(sender.sender()).entries().map { Suggestion.suggestion(it.syntax())}
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("players") { _, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(Bukkit.getOnlinePlayers()
                .map { Suggestion.suggestion(it.name) }
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, _ ->
            return@registerSuggestionProvider CompletableFuture.completedFuture(Bukkit.getOfflinePlayers()
                .map { Suggestion.suggestion(it.name.toString()) }
            )
        }
        commandManager.parserRegistry().registerSuggestionProvider("materials") {_, _ ->
            return@registerSuggestionProvider CompletableFuture
                .completedFuture(Material.entries.map { Suggestion.suggestion(it.name) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("worlds") {_, _ ->
            return@registerSuggestionProvider CompletableFuture
                .completedFuture(Bukkit.getWorlds().map { Suggestion.suggestion(it.name) })
        }
    }

    fun registerCommands() {
        try {
            magenta.logger.info("Registering commands with Cloud Command Framework !")
            val commandManager = createCommandManager()
            registerMinecraftExceptionHandler(commandManager)
            helpManager(commandManager)

            registerGlobalSuggestionProviders(commandManager)
            val annotationParser = createAnnotationParser(commandManager)
            val register = AnnotationCommandRegister(magenta, annotationParser, commandManager)

            register.registerCommand(
                AfkCmd(magenta),
                BackCmd(magenta),
                BroadcastCmd(magenta),
                CommandItemsCmd(magenta),
                ContainersCmd(magenta),
                EnderChestCmd(magenta),
                FeedbackCmd(magenta),
                FlyCmd(magenta),
                GmCmd(magenta),
                HatCmd(magenta),
                HealCmd(magenta),
                HelpOpCmd(magenta),
                HomeCmd(magenta),
                IgnoreCmd(magenta),
                InvseeCmd(magenta),
                JailCmd(magenta),
                KitCmd(magenta),
                LevelCmd(magenta),
                LevelsCmd(magenta),
                LightningCmd(magenta),
                MagentaCmd(magenta),
                OresCmd(magenta),
                RandomCmd(magenta),
                MsgCmd(magenta),
                RepairCmd(magenta),
                ReportCmd(magenta),
                ShopCmd(magenta),
                SocialSpyCmd(magenta),
                SpawnerCmd(magenta),
                TpCmd(magenta),
                VanishCmd(magenta),
                VipCmd(magenta),
                VoteCmd(magenta),
                VotesCmd(magenta),
                WarpCmd(magenta),
                WhoisCmd(magenta)
            )
        } catch (e : NoClassDefFoundError) {
            magenta.logger.severe(e.message ?: e.localizedMessage)
        }
    }

}