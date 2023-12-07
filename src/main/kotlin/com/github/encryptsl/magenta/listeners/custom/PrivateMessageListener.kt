package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PrivateMessageListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPrivateMessage(event: PrivateMessageEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val message = event.message

        if (commandSender.name == target.name)
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.msg.error.yourself")))

        val user = magenta.user.getUser(target.uniqueId)

        if (commandSender is Player) {
            if (user.getAccount().getStringList("ignore").contains(commandSender.player?.uniqueId.toString())) {
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.msg.error")))
                event.isCancelled = true
            }
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
            PlayerBuilderAction
                .player(target)
                .sound(Sound.valueOf(magenta.config.getString("msg.sound").toString()),
                    magenta.config.getString("msg.volume").toString().toFloat(),
                    magenta.config.getString("pitch.volume").toString().toFloat()
                ).message(ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.msg.success.from"), TagResolver.resolver(
                        Placeholder.parsed("player", commandSender.name),
                        Placeholder.parsed("message", message),
                    )
                ))
        }
    }

}