package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.home.*
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.utils.ModernText
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

        val worldsWhitelist = magenta.config.getStringList("homes.whitelist")

        if ((!worldsWhitelist.contains(location.world.name) && !worldsWhitelist.contains("*")) && !player.hasPermission("magenta.home.whitelist.exempt"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.blocked",
                    TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        if (magenta.homeModel.getHomeExist(player.uniqueId, homeName))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.exist", Placeholder.parsed("home", homeName)))

        if (!magenta.homeModel.canSetHome(player))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.limit"))

        magenta.homeModel.createHome(player, location, homeName)
        player.sendMessage(magenta.localeConfig.translation("magenta.command.home.success.created", Placeholder.parsed("home", homeName)))
    }

    @EventHandler
    fun onHomeDelete(event: HomeDeleteEvent) {
        val player: Player = event.player
        val homeName: String = event.homeName

        val worldsWhitelist = magenta.config.getStringList("homes.whitelist")

        if (!worldsWhitelist.contains(player.location.world.name) && !worldsWhitelist.contains("*"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.blocked",
                Placeholder.parsed("world", player.location.world.name)))

        if (!magenta.homeModel.getHomeExist(player.uniqueId, homeName))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.not.exist", Placeholder.parsed("home", homeName)))

        magenta.homeModel.deleteHome(player.uniqueId, homeName)
        player.sendMessage(magenta.localeConfig.translation("magenta.command.home.success.deleted", Placeholder.parsed("home", homeName)))
    }

    @EventHandler
    fun onHomeInfo(event: HomeInfoEvent) {
        val player = event.player
        val infoType = event.infoType

        when(infoType) {
            InfoType.LIST -> {
                val list = magenta.homeModel.getHomesByOwner(player.uniqueId).joinToString { s ->
                    magenta.localeConfig.getMessage("magenta.command.home.success.list.component")
                        .replace("<home>", s.homeName)
                        .replace("info", magenta.config.getString("home-info-format").toString()
                            .replace("<home>", s.homeName)
                            .replace("<x>", s.x.toString())
                            .replace("<y>", s.y.toString())
                            .replace("<z>", s.z.toString())
                            .replace("<world>", s.world)
                        )
                }
                player.sendMessage(magenta.localeConfig.translation("magenta.command.home.success.list",
                    Placeholder.component("homes", ModernText.miniModernText(list)),
                ))
            }
            InfoType.INFO -> {
                val homeName = event.homeName ?: return
                if (!magenta.homeModel.getHomeExist(player.uniqueId, homeName))
                    return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.not.exist",
                        Placeholder.parsed("home", homeName))
                    )

                magenta.config.getStringList("home-info-format").forEach { s ->
                    player.sendMessage(ModernText.miniModernText(s, TagResolver.resolver(
                        Placeholder.parsed("home", magenta.homeModel.getHome(homeName, HomeTable.home)),
                        Placeholder.parsed("owner", magenta.homeModel.getHome(homeName, HomeTable.username)),
                        Placeholder.parsed("world", magenta.homeModel.getHome(homeName, HomeTable.world)),
                        Placeholder.parsed("x", magenta.homeModel.getHome(homeName, HomeTable.x).toString()),
                        Placeholder.parsed("y", magenta.homeModel.getHome(homeName, HomeTable.y).toString()),
                        Placeholder.parsed("z", magenta.homeModel.getHome(homeName, HomeTable.z).toString()),
                    )))
                }
            }
        }
    }

    @EventHandler
    fun onHomeMoveLocation(event: HomeMoveLocationEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val homeName: String = event.homeName

        val worldsWhitelist = magenta.config.getStringList("homes.whitelist")

        if (!worldsWhitelist.contains(player.location.world.name) && !worldsWhitelist.contains("*"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.blocked",
                Placeholder.parsed("world", player.location.world.name)))

        if (!magenta.homeModel.getHomeExist(player.uniqueId, homeName))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.not.exist", Placeholder.parsed("home", homeName)))

        magenta.homeModel.moveHome(player.uniqueId, homeName, location)
        player.sendMessage(magenta.localeConfig.translation("magenta.command.home.success.moved", TagResolver.resolver(
            Placeholder.parsed("home", homeName),
            Placeholder.parsed("x", location.x.toInt().toString()),
            Placeholder.parsed("y", location.y.toInt().toString()),
            Placeholder.parsed("z", location.z.toInt().toString())
        )))
    }

    @EventHandler
    fun onHomeRename(event: HomeRenameEvent) {
        val player: Player = event.player
        val location: Location = player.location
        val oldHomeName: String = event.fromHomeName
        val newHomeName: String = event.toHomeName

        val worldsWhitelist = magenta.config.getStringList("homes.whitelist")

        if (!worldsWhitelist.contains(player.location.world.name) && !worldsWhitelist.contains("*"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.blocked",
                Placeholder.parsed("world", location.world.name)))

        if (!magenta.homeModel.getHomeExist(player.uniqueId, oldHomeName))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.not.exist",
                Placeholder.parsed("home", oldHomeName)))

        magenta.homeModel.renameHome(player.uniqueId, oldHomeName, newHomeName)
        player.sendMessage(magenta.localeConfig.translation("magenta.command.home.success.renamed", TagResolver.resolver(
            Placeholder.parsed("new_home", newHomeName),
            Placeholder.parsed("old_home", oldHomeName)
        )))
    }
    @EventHandler
    fun onHomeTeleport(event: HomeTeleportEvent) {
        val homeName = event.homeName
        val player: Player = event.player
        val delay = event.delay
        val location: Location = player.location
        val user = magenta.user.getUser(player.uniqueId)

        val worldsWhitelist = magenta.config.getStringList("homes.whitelist")

        if (!worldsWhitelist.contains(player.location.world.name) && !worldsWhitelist.contains("*"))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.blocked",
                Placeholder.parsed("world", location.world.name)))

        if (!magenta.homeModel.getHomeExist(player.uniqueId, homeName))
            return player.sendMessage(magenta.localeConfig.translation("magenta.command.home.error.not.exist",
                Placeholder.parsed("home", homeName)))

        val timeLeft: Duration = user.getRemainingCooldown("home")

        if (user.hasDelay("home") && !player.hasPermission("magenta.home.delay.exempt")) {
            return commandHelper.delayMessage(player, "magenta.command.home.error.delay", timeLeft)
        }

        if (delay != 0L && delay != -1L || !player.hasPermission("magenta.home.delay.exempt")) {
            user.setDelay(Duration.ofSeconds(delay), "home")
        }

        player.teleport(magenta.homeModel.toLocation(player, homeName))

        player.sendMessage(magenta.localeConfig.translation("magenta.command.home.success.teleport", Placeholder.parsed("home", homeName)))
    }

}