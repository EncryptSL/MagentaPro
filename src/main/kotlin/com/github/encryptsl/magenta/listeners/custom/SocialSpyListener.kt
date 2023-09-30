package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByAdminEvent
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByPlayerEvent
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SocialSpyListener(private val magenta: Magenta) : Listener {

    private val commandHelper = CommandHelper(magenta)

    @EventHandler
    fun onSocialSpyByPlayer(event: SpyToggleByPlayerEvent) {
        val player = event.player
        val account = PlayerAccount(magenta, player.uniqueId)
        if (account.getAccount().getBoolean("socialspy")) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.social.spy.success.toggle"),
                Placeholder.parsed("value", false.toString())
            ))
            commandHelper.toggleSocialSpy(player, false)
        } else {
            commandHelper.toggleSocialSpy(player, true)
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.social.spy.success.toggle"),
                Placeholder.parsed("value", true.toString())
            ))
        }

    }

    @EventHandler
    fun onSocialSpyByAdmin(event: SpyToggleByAdminEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val account = PlayerAccount(magenta, target.uniqueId)
        if (account.getAccount().getBoolean("socialspy")) {
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.social.spy.success.toggle"),
                Placeholder.parsed("value", false.toString())
            ))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.social.spy.success.toggle.to"),
                Placeholder.parsed("value", false.toString())
            ))
            commandHelper.toggleSocialSpy(target, false)
        } else {
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.social.spy.success.toggle"),
                Placeholder.parsed("value", true.toString())
            ))
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.social.spy.success.toggle.to"),
                Placeholder.parsed("value", true.toString())
            ))
            commandHelper.toggleSocialSpy(target, true)
        }
    }

}