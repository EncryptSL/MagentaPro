package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class JailPardonListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailRelease(event: JailPardonEvent) {
        val target = event.player
        val playerAccount = PlayerAccount(magenta, target.uniqueId)

        val player = Bukkit.getPlayer(target.uniqueId)

        if (player != null) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.unjailed")))
            magenta.schedulerMagenta.runTask(magenta) {
                val world = playerAccount.getAccount().getString("lastlocation.world-name").toString()
                val x = playerAccount.getAccount().getDouble("lastlocation.x")
                val y = playerAccount.getAccount().getDouble("lastlocation.y")
                val z = playerAccount.getAccount().getDouble("lastlocation.z")
                val pitch = playerAccount.getAccount().getDouble("lastlocation.pitch")
                val yaw = playerAccount.getAccount().getDouble("lastlocation.yaw")

                magenta.pluginManager.callEvent(
                    JailTeleportEvent(
                        player,
                        Location(Bukkit.getWorld(world), x, y, z, pitch.toFloat(), yaw.toFloat())
                    )
                )
            }
        }
        Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.unjailed.to"), TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString())
                )
            ), "magenta.jail.pardon.event"
        )
        playerAccount.cooldownManager.setCooldown(Duration.ofSeconds(0), "jail")
        playerAccount.getAccount().set("jailed", false)
        playerAccount.save()
    }

}