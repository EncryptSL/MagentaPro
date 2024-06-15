package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.*
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class HomeListeners(private val magenta: Magenta) : Listener {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }
    @EventHandler
    fun onHomeCreate(event: HomeCreateEvent) {
        val homeName = event.homeName
        val player: Player = event.player
        val location: Location = event.location

        val isInListWorld = magenta.stringUtils.inInList("homes.whitelist", location.world.name)
        val isInListAll = magenta.stringUtils.inInList("homes.whitelist", "*")

        if ((!isInListWorld && !isInListAll) && !player.hasPermission(Permissions.HOME_WHITELIST_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.home.error.blocked",
                    TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        if (!magenta.homeModel.canSetHome(player).join())
            return player.sendMessage(magenta.locale.translation("magenta.command.home.error.limit"))

        magenta.homeModel.getHomeByNameAndUUID(player.uniqueId, homeName).thenApply {
            player.sendMessage(magenta.locale.translation("magenta.command.home.error.exist", Placeholder.parsed("home", it.homeName)))
        }.exceptionally {
            magenta.homeModel.createHome(player, location, homeName)
            player.sendMessage(magenta.locale.translation("magenta.command.home.success.created", Placeholder.parsed("home", homeName)))
        }
    }

    @EventHandler
    fun onHomeDelete(event: HomeDeleteEvent) {
        val player: Player = event.player
        val homeName: String = event.homeName

        val isInListWorld = magenta.stringUtils.inInList("homes.whitelist", player.location.world.name)
        val isInListAll = magenta.stringUtils.inInList("homes.whitelist", "*")

        if ((!isInListWorld && !isInListAll) && !player.hasPermission(Permissions.HOME_WHITELIST_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.home.error.blocked",
                TagResolver.resolver(Placeholder.parsed("world", player.location.world.name))))

        magenta.homeModel.getHomeByNameAndUUID(player.uniqueId, homeName).thenApply {
            magenta.homeModel.deleteHome(player.uniqueId, homeName)
            player.sendMessage(magenta.locale.translation("magenta.command.home.success.deleted", Placeholder.parsed("home", homeName)))
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.home.error.not.exist", Placeholder.parsed("home", homeName)))
        }
    }

    @EventHandler
    fun onHomeMoveLocation(event: HomeMoveLocationEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val homeName: String = event.homeName

        val isInListWorld = magenta.stringUtils.inInList("homes.whitelist", player.location.world.name)
        val isInListAll = magenta.stringUtils.inInList("homes.whitelist", "*")

        if ((!isInListWorld && !isInListAll) && !player.hasPermission(Permissions.HOME_WHITELIST_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.home.error.blocked",
                TagResolver.resolver(Placeholder.parsed("world", player.location.world.name))))

        magenta.homeModel.getHomeByNameAndUUID(player.uniqueId, homeName).thenApply {
            magenta.homeModel.moveHome(player.uniqueId, homeName, location)
            player.sendMessage(magenta.locale.translation("magenta.command.home.success.moved", TagResolver.resolver(
                Placeholder.parsed("home", it.homeName),
                Placeholder.parsed("x", location.x.toInt().toString()),
                Placeholder.parsed("y", location.y.toInt().toString()),
                Placeholder.parsed("z", location.z.toInt().toString())
            )))
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.home.error.not.exist", Placeholder.parsed("home", homeName)))
        }
    }

    @EventHandler
    fun onHomeRename(event: HomeRenameEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val oldHomeName: String = event.fromHomeName
        val newHomeName: String = event.toHomeName

        val isInListWorld = magenta.stringUtils.inInList("homes.whitelist", location.world.name)
        val isInListAll = magenta.stringUtils.inInList("homes.whitelist", "*")

        if ((!isInListWorld && !isInListAll) && !player.hasPermission(Permissions.HOME_WHITELIST_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.home.error.blocked",
                TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        magenta.homeModel.getHomeByNameAndUUID(player.uniqueId, oldHomeName).thenApply {
            magenta.homeModel.renameHome(player.uniqueId, oldHomeName, newHomeName)
            player.sendMessage(magenta.locale.translation("magenta.command.home.success.renamed", TagResolver.resolver(
                Placeholder.parsed("new_home", newHomeName),
                Placeholder.parsed("old_home", oldHomeName)
            )))
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.home.error.not.exist", Placeholder.parsed("home", oldHomeName)))
        }
    }
    @EventHandler
    fun onHomeTeleport(event: HomeTeleportEvent) {
        val homeName = event.homeName
        val player: Player = event.player
        val delay = event.delay
        val location: Location = player.location
        val user = magenta.user.getUser(player.uniqueId)


        val isInListWorld = magenta.stringUtils.inInList("homes.whitelist", location.world.name)
        val isInListAll = magenta.stringUtils.inInList("homes.whitelist", "*")

        if ((!isInListWorld && !isInListAll) && !player.hasPermission(Permissions.HOME_WHITELIST_EXEMPT))
            return player.sendMessage(magenta.locale.translation("magenta.command.home.error.blocked",
                TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        val timeLeft: Duration = user.getRemainingCooldown("home")

        if (user.hasDelay("home") && !player.hasPermission("magenta.home.delay.exempt")) {
            return commandHelper.delayMessage(player, "magenta.command.home.error.delay", timeLeft)
        }

        if (delay != 0L && delay != -1L || !player.hasPermission("magenta.home.delay.exempt")) {
            user.setDelay(Duration.ofSeconds(delay), "home")
        }

        magenta.homeModel.getHomeByNameAndUUID(player.uniqueId, homeName).thenApply {
            player.teleport(magenta.homeModel.toLocation(player, it.homeName))

            player.sendMessage(magenta.locale.translation("magenta.command.home.success.teleport", Placeholder.parsed("home", it.homeName)))
        }.exceptionally {
            player.sendMessage(magenta.locale.translation("magenta.command.home.error.not.exist", Placeholder.parsed("home", homeName)))
        }
    }

}