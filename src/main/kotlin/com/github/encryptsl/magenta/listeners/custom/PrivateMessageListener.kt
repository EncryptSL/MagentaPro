package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.FastReplyMessageEvent
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PrivateMessageListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPrivateMessage(event: PrivateMessageEvent) {
        val commandSender = event.commandSender
        val receiver = event.receiver
        val message = event.message
        val reply = event.reply

        if (commandSender.name == receiver.name)
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.msg.error.yourself")))

        val user = magenta.user.getUser(receiver.uniqueId)

        if (commandSender is Player) {
            val whisper = magenta.user.getUser(commandSender.uniqueId)

            val userHaveBlockedSender = user.isPlayerIgnored(commandSender.uniqueId)
            val whisperHaveBlockedTarget = whisper.isPlayerIgnored(receiver.uniqueId)

            if (userHaveBlockedSender || whisperHaveBlockedTarget) {
                commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.msg.error")))
                event.isCancelled = true
            }
        }

        if (!event.isCancelled) {
            if (commandSender is Player) {
                reply.put(commandSender, receiver)
                reply.put(receiver, commandSender)
            }

            commandSender.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.command.msg.success.to"), TagResolver.resolver(
                        Placeholder.parsed("player", receiver.name),
                        Placeholder.parsed("message", message),
                    )
                )
            )
            PlayerBuilderAction
                .player(receiver)
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

    @EventHandler
    fun onFastReplyMessage(event: FastReplyMessageEvent) {
        val commandSender = event.commandSender
        val receiver = event.receiver
        val message = event.message

        if (receiver == null || Bukkit.getPlayer(receiver.uniqueId) == null)
            return commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.reply.error.empty")))

        commandSender.sendMessage(
            ModernText.miniModernText(
                magenta.localeConfig.getMessage("magenta.command.msg.success.to"), TagResolver.resolver(
                    Placeholder.parsed("player", receiver.name),
                    Placeholder.parsed("message", message),
                )
            )
        )

        PlayerBuilderAction
            .player(receiver)
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