package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
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
        val account = PlayerAccount(magenta, player.uniqueId)

        if (account.getAccount().getStringList("ignore").contains(target.uniqueId.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exist"),
                Placeholder.parsed("player", target.uniqueId.toString())
            ))

        if (target.player?.hasPermission("essentials.ignore.exempt") == true)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exempt"),
                Placeholder.parsed("player", target.uniqueId.toString())
            ))

        account.getAccount().set("ignore", listOf(target.player))
        account.save()
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success"),
            Placeholder.parsed("player", target.name.toString())
        ))
    }

    @EventHandler
    fun onPlayerIgnore(event: PlayerIgnoreEvent) {
        val player = event.player
        val account = PlayerAccount(magenta, player.uniqueId)
        event.isCancelled = account.getAccount().getStringList("ignore").contains(player.uniqueId.toString())
    }
    @EventHandler
    fun onPlayerRemoveIgnore(event: PlayerRemoveIgnoreEvent) {
        val player = event.player
        val target = event.target
        val account = PlayerAccount(magenta, player.uniqueId)

        if (!account.getAccount().getStringList("ignore").contains(target.uniqueId.toString()))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.error.exist"),
                Placeholder.parsed("player", target.uniqueId.toString())
            ))

        account.getAccount().set("ignore", account.getAccount().getStringList("ignore").remove(target.uniqueId.toString()))
        account.save()
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.ignore.success"),
            Placeholder.parsed("player", target.name.toString())
        ))

    }


}