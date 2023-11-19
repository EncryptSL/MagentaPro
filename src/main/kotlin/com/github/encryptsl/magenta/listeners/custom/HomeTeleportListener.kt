package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class HomeTeleportListener(private val magenta: Magenta) : Listener {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }

    @EventHandler
    fun onHomeTeleport(event: HomeTeleportEvent) {
        val homeName = event.homeName
        val player: Player = event.player
        val delay = event.delay
        val location: Location = player.location
        val user = magenta.user.getUser(player.uniqueId)

        val worlds = magenta.config.getStringList("homes.whitelist").contains(player.location.world.name)

        if (!worlds)
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.blocked"),
                    TagResolver.resolver(Placeholder.parsed("world", location.world.name))))

        if (!magenta.homeModel.getHomeExist(player, homeName))
            return player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.error.not.exist"),
                    TagResolver.resolver(Placeholder.parsed("home", homeName))))

        val timeLeft: Duration = user.cooldownManager.getRemainingDelay("home")

        if (user.cooldownManager.hasDelay("home") && !player.hasPermission("magenta.home.delay.exempt")) {
            return commandHelper.delayMessage(player, "magenta.command.home.error.delay", timeLeft)
        }

        if (delay != 0L && delay != -1L || !player.hasPermission("magenta.home.delay.exempt")) {
            user.cooldownManager.setDelay(Duration.ofSeconds(delay), "home")
            user.save()
        }

        magenta.homeModel.getHomesByOwner(player).filter { s -> s.homeName == homeName }.first {
            player.teleport(Location(
                Bukkit.getWorld(it.world),
                it.x.toDouble(),
                it.y.toDouble(),
                it.z.toDouble(),
                it.yaw, it.pitch)
            )
        }

        player.sendMessage(
            ModernText.miniModernText(
                magenta.localeConfig.getMessage("magenta.command.home.success.teleport"),
                TagResolver.resolver(Placeholder.parsed("home", homeName))
            )
        )
    }
}