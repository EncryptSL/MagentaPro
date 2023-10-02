package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.api.events.jail.JailTeleportEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class JailPardonListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onJailRelease(event: JailPardonEvent) {
        val target = event.player
        val playerAccount = PlayerAccount(magenta, target.uniqueId)

        val player = Bukkit.getPlayer(target.uniqueId)

        if (player != null) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.unjailed")))
            magenta.pluginManager.callEvent(JailTeleportEvent(player, playerAccount.getLastLocation()))
        }
        Bukkit.broadcast(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.success.unjailed.to"), TagResolver.resolver(
                    Placeholder.parsed("player", target.name.toString())
                )
            ), "magenta.jail.pardon.event"
        )

        playerAccount.cooldownManager.removeDelay("jail")
        playerAccount.cooldownManager.removeDelay("onlinejail")
    }

}