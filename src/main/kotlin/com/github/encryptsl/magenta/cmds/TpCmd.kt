package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.api.events.teleport.TpaAcceptEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaDenyEvent
import com.github.encryptsl.magenta.api.events.teleport.TpaRequestEvent
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class TpCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val commandHelper = CommandHelper(magenta)

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("tpa <target>")
    @Permission("magenta.tpa")
    @CommandDescription("This command send teleport request to player")
    fun onTpa(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        magenta.pluginManager.callEvent(TpaRequestEvent(player, target))
    }

    @Command("tpaccept")
    @Permission("magenta.tpaccept")
    @CommandDescription("This command accept teleport request from player")
    fun onTpaAccept(player: Player) {
        magenta.pluginManager.callEvent(TpaAcceptEvent(player))
    }

    @Command("tpadeny")
    @Permission("magenta.tpadeny")
    @CommandDescription("This command cancel teleport request")
    fun onTpaDeny(player: Player) {
        magenta.pluginManager.callEvent(TpaDenyEvent(player))
    }


    @Command("tp <player>")
    @Permission("magenta.tp")
    @CommandDescription("This command teleport you to other player")
    fun onTeleport(player: Player, @Argument(value = "player", suggestions = "players") target: Player) {
        val account = magenta.user.getUser(target.uniqueId)
        if (!account.getAccount().getBoolean("teleportenabled") && !player.hasPermission(Permissions.TELEPORT_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.tp.error.exempt",
                Placeholder.parsed("player", player.name)
            ))

        if (player.uniqueId == target.uniqueId)
            return player.sendMessage(magenta.locale.translation("magenta.command.tp.error.yourself"))

        player.teleport(target, PlayerTeleportEvent.TeleportCause.COMMAND)

        player.sendMessage(magenta.locale.translation("magenta.command.tp.success.self", Placeholder.parsed("target", target.name)))
    }

    @Command("tp <player> <target>")
    @Permission("magenta.tp.other")
    @CommandDescription("This command teleport player to player")
    fun onTeleportConsole(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        val targetAccount = magenta.user.getUser(target.uniqueId)
        if (!targetAccount.getAccount().getBoolean("teleportenabled") && !commandSender.hasPermission(Permissions.TELEPORT_EXEMPT))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.tp.error.exempt",
                Placeholder.parsed("player", player.name)
            ))

        if (player.uniqueId == target.uniqueId)
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.tp.error.yourself.same"))

        player.teleport(target.location, PlayerTeleportEvent.TeleportCause.COMMAND)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.tp.success.to", TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("target", target.name)
        )))
        player.sendMessage(magenta.locale.translation("magenta.command.tp.success.self", Placeholder.parsed("target", target.name)))
    }

    @Command("tpo <target>")
    @Permission("magenta.tpo")
    @CommandDescription("This command teleport you to other player logout location")
    fun onTeleportOfflineLocation(player: Player, @Argument(value = "target", suggestions = "offlinePlayers") offlinePlayer: OfflinePlayer) {
        commandHelper.teleportOffline(player, offlinePlayer)

        player.sendMessage(magenta.locale.translation("magenta.command.tpo.success",
            Placeholder.parsed("player", offlinePlayer.name.toString())
        ))
    }

    @Command("tphere [target]")
    @Permission("magenta.tphere")
    @CommandDescription("This command teleport player to you")
    fun onTeleportHere(player: Player, @Argument(value = "target") target: Player?) {

        if (target == null) {
            commandHelper.teleportAll(player, HashSet(Bukkit.getOnlinePlayers()))
            return player.sendMessage(magenta.locale.translation("magenta.command.tpall.success"))
        }

        target.teleport(player, PlayerTeleportEvent.TeleportCause.COMMAND)
        player.sendMessage(magenta.locale.translation("magenta.command.tphere.success",
            Placeholder.parsed("player", target.name)
        ))
    }

    @Command("tpall [world]")
    @Permission("magenta.tpall")
    @CommandDescription("This command teleport player from other or current world to you")
    fun onTeleportAllHere(player: Player, @Argument("world", suggestions = "worlds") world: World?) {

        if (world != null) {
            commandHelper.teleportAll(player, HashSet(world.players))
            return player.sendMessage(magenta.locale.translation("magenta.command.tpall.world.success",
                Placeholder.parsed("world", world.name)
            ))
        }

        commandHelper.teleportAll(player, HashSet(Bukkit.getOnlinePlayers()))
        player.sendMessage(magenta.locale.translation("magenta.command.tpall.success"))
    }

}