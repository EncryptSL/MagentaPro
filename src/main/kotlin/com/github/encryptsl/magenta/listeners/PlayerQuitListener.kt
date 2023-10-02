package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val playerAccount = PlayerAccount(magenta, player.uniqueId)

        if (magenta.config.getString("custom-quit-message") != "none") {
            event.quitMessage(ModernText.miniModernText(
                    magenta.config.getString("custom-quit-message").toString(), TagResolver.resolver(
                        Placeholder.component("player", player.displayName())
                    )
            ))
        }

        playerAccount.getAccount().set("timestamps.logout", System.currentTimeMillis())
        playerAccount.getAccount().set("logoutlocation.world-name", player.world.name)
        playerAccount.getAccount().set("logoutlocation.x", player.location.x)
        playerAccount.getAccount().set("logoutlocation.y", player.location.y)
        playerAccount.getAccount().set("logoutlocation.z", player.location.z)
        playerAccount.getAccount().set("logoutlocation.yaw", player.location.yaw)
        playerAccount.getAccount().set("logoutlocation.pitch", player.location.pitch)
        playerAccount.save()
    }

}