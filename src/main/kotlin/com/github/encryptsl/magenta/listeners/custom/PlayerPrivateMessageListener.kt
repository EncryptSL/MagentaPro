package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.ignore.PlayerIgnoreEvent
import com.github.encryptsl.magenta.api.events.pm.PlayerPrivateMessageEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerPrivateMessageListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPrivateMessage(event: PlayerPrivateMessageEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val message = event.message
        val playerIgnoreEvent = PlayerIgnoreEvent(target)

        if (commandSender.name == target.name)
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.msg.error.yourself")))

        playerIgnoreEvent.callEvent()
        if (playerIgnoreEvent.isCancelled) {
            commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.msg.error")))
            event.isCancelled = true
        }

        if (!event.isCancelled) {
            commandSender.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.msg.success.to"), TagResolver.resolver(
                        Placeholder.parsed("player", target.name),
                        Placeholder.parsed("message", message),
                    )
                )
            )
            target.playSound(
                target, Sound.valueOf(magenta.config.getString("msg.sound").toString()),
                magenta.config.getString("msg.volume").toString().toFloat(),
                magenta.config.getString("msg.pitch").toString().toFloat()
            )
            target.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.msg.success.from"), TagResolver.resolver(
                        Placeholder.parsed("player", commandSender.name),
                        Placeholder.parsed("message", message),
                    )
                )
            )
        }
    }

}