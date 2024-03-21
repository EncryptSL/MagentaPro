package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByAdminEvent
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByPlayerEvent
import com.github.encryptsl.magenta.common.CommandHelper
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SocialSpyListener(private val magenta: Magenta) : Listener {

    private val commandHelper = CommandHelper(magenta)

    @EventHandler
    fun onSocialSpyByPlayer(event: SpyToggleByPlayerEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        if (user.isSocialSpy()) {
            player.sendMessage(magenta.localeConfig.translation("magenta.command.social.spy.success.toggle",
                Placeholder.parsed("value", false.toString())
            ))
            commandHelper.toggleSocialSpy(player, false)
        } else {
            commandHelper.toggleSocialSpy(player, true)
            player.sendMessage(magenta.localeConfig.translation("magenta.command.social.spy.success.toggle",
                Placeholder.parsed("value", true.toString())
            ))
        }
    }

    @EventHandler
    fun onSocialSpyByAdmin(event: SpyToggleByAdminEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val user = magenta.user.getUser(target.uniqueId)
        if (user.isSocialSpy()) {
            target.sendMessage(magenta.localeConfig.translation("magenta.command.social.spy.success.toggle",
                Placeholder.parsed("value", false.toString())
            ))
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.social.spy.success.toggle.to",
                TagResolver.resolver(
                    Placeholder.parsed("player", target.name),
                    Placeholder.parsed("value", true.toString())
                )
            ))
            commandHelper.toggleSocialSpy(target, false)
        } else {
            target.sendMessage(magenta.localeConfig.translation("magenta.command.social.spy.success.toggle",
                Placeholder.parsed("value", true.toString())
            ))
            commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.social.spy.success.toggle.to", TagResolver.resolver(
                Placeholder.parsed("player", target.name),
                Placeholder.parsed("value", true.toString())
            )))
            commandHelper.toggleSocialSpy(target, true)
        }
    }

}