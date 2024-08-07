package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.suggestion.Suggestion
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.*
import com.github.encryptsl.magenta.api.menu.modules.home.HomeGUI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class HomeCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val homeMenuGUI: HomeGUI by lazy { HomeGUI(magenta) }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("homes") { sender, _ ->
            val player = sender.sender() as Player
            return@registerSuggestionProvider CompletableFuture
                .completedFuture(magenta.homeModel.getHomesByOwner(player.uniqueId).join().map { s -> Suggestion.suggestion(s.homeName) })
        }
        commandManager.parserRegistry().registerSuggestionProvider("homeIcons") {_, _ ->
            return@registerSuggestionProvider CompletableFuture
                .completedFuture(magenta.homeEditorConfig.getConfig().getStringList("menu.icons")
                    .map { Suggestion.suggestion(it) }
                )
        }
        annotationParser.parse(this)
    }

    @Command("home <home>")
    @Permission("magenta.home")
    @CommandDescription("This command teleport you into your home")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.pluginManager.callEvent(HomeTeleportEvent(player, home, magenta.config.getLong("teleport-cooldown")))
    }

    @Command("sethome <home>")
    @Permission("magenta.sethome")
    @CommandDescription("This command create your home where you stand.")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {
        magenta.pluginManager.callEvent(HomeCreateEvent(player, player.location, home))
    }

    @Command("sethomeicon|sethicon <home> <icon>")
    @Permission("magenta.sethomeicon")
    @CommandDescription("This command set home icon visible in your gui homelist")
    fun onSetHomeIcon(player: Player, @Argument(value = "home", suggestions = "homes") home: String, @Argument("icon", suggestions = "homeIcons") icon: String) {
        magenta.homeModel.setHomeIcon(player.uniqueId, home, icon)

        player.sendMessage(magenta.locale.translation("magenta.command.home.success.change.icon", TagResolver.resolver(
            Placeholder.parsed("home", home),
            Placeholder.parsed("icon", icon)
        )))
    }

    @Command("movehome|mhome <home>")
    @Permission("magenta.movehome")
    @CommandDescription("This command move your home to location where you stand.")
    fun onMoveHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.pluginManager.callEvent(HomeMoveLocationEvent(player, player.location, home))
    }

    @Command("delhome|dhome <home>")
    @Permission("magenta.delhome")
    @CommandDescription("This command delete your home.")
    fun onDeleteHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        magenta.pluginManager.callEvent(HomeDeleteEvent(player, home))
    }

    @Command("renamehome|rhome <oldName> <newName>")
    @Permission("magenta.rename.home")
    @CommandDescription("This command rename your home.")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {
        magenta.pluginManager.callEvent(HomeRenameEvent(player, oldName, newName))
    }

    @Command("homes|homelist")
    @Permission("magenta.home.list")
    @CommandDescription("This command open gui list or chat list.")
    fun onHomeList(player: Player) {
        homeMenuGUI.openHomeGUI(player)
    }
}