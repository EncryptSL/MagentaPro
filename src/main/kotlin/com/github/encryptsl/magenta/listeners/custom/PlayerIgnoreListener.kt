package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.ignore.PlayerIgnoreEvent
import com.github.encryptsl.magenta.api.events.ignore.PlayerInsertIgnoreEvent
import com.github.encryptsl.magenta.api.events.ignore.PlayerRemoveIgnoreEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerIgnoreListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onStartIgnoring(event: PlayerInsertIgnoreEvent) {
        val player = event.player
        val target = event.target
        val user = magenta.user.getUser(player.uniqueId)


        if (player.uniqueId == target.uniqueId)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.yourself")))

        if (user.getAccount().getStringList("ignore").contains(target.uniqueId.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exist"),
                Placeholder.parsed("player", target.name.toString())
            ))


        if (target.player?.hasPermission("magenta.ignore.exempt") == true)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exempt"),
                Placeholder.parsed("player", target.name.toString())
            ))

        user.set("ignore", setOf(target.uniqueId.toString()))
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success"),
            Placeholder.parsed("player", target.name.toString())
        ))
    }

    @EventHandler
    fun onPlayerIgnore(event: PlayerIgnoreEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        event.isCancelled = user.getAccount().getStringList("ignore").contains(player.uniqueId.toString())
    }
    @EventHandler
    fun onPlayerRemoveIgnore(event: PlayerRemoveIgnoreEvent) {
        val player = event.player
        val target = event.target
        val user = magenta.user.getUser(player.uniqueId)

        if (!user.getAccount().getStringList("ignore").contains(target.uniqueId.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.not.exist"),
                Placeholder.parsed("player", target.name.toString()))
            )

        val list: MutableList<String> = user.getAccount().getStringList("ignore")
        list.remove(target.uniqueId.toString())
        user.set("ignore", list)
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success.removed"),
            Placeholder.parsed("player", target.name.toString())
        ))

    }


}