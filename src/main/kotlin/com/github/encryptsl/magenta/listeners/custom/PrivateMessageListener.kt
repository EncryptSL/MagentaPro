package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.pm.FastReplyMessageEvent
import com.github.encryptsl.magenta.api.events.pm.PrivateMessageEvent
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PrivateMessageListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onPrivateMessage(event: PrivateMessageEvent) {
        val commandSender = event.commandSender
        val receiver = event.receiver
        val message = event.message
        val cacheManager = event.playerCacheManager

        if (commandSender.name == receiver.name)
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.msg.error.yourself"))

        val receiverUser = magenta.user.getUser(receiver.uniqueId)

        if (commandSender is Player) {
            val whisper = magenta.user.getUser(commandSender.uniqueId)

            val userHaveBlockedSender = receiverUser.isPlayerIgnored(commandSender.uniqueId)
            val whisperHaveBlockedTarget = whisper.isPlayerIgnored(receiver.uniqueId)

            if (receiverUser.isVanished() && !commandSender.hasPermission("magenta.vanish.exempt")) {
                event.isCancelled = true
            }

            if (userHaveBlockedSender || whisperHaveBlockedTarget) {
                commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.msg.error"))
                event.isCancelled = true
            }
        }

        if (!event.isCancelled) {
            if (commandSender is Player) {
                cacheManager.reply.put(commandSender, receiver)
                cacheManager.reply.put(receiver, commandSender)
            }

            sendMessage(commandSender, receiver, message)
        }
    }

    @EventHandler
    fun onFastReplyMessage(event: FastReplyMessageEvent) {
        val commandSender = event.commandSender
        val receiver = event.receiver
        val message = event.message

        if (receiver == null || Bukkit.getPlayer(receiver.uniqueId) == null)
            return commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.reply.error.empty"))

        val user = magenta.user.getUser(receiver.uniqueId)
        if (commandSender is Player) {
            val whisper = magenta.user.getUser(commandSender.uniqueId)

            val userHaveBlockedSender = user.isPlayerIgnored(commandSender.uniqueId)
            val whisperHaveBlockedTarget = whisper.isPlayerIgnored(receiver.uniqueId)

            if (userHaveBlockedSender || whisperHaveBlockedTarget) {
                commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.msg.error"))
                event.isCancelled = true
            }
        }

        if (!event.isCancelled) {
            sendMessage(commandSender, receiver, message)
        }

    }

    private fun sendMessage(commandSender: CommandSender, receiver: Player, message: String) {
        commandSender.sendMessage(magenta.localeConfig.translation("magenta.command.msg.success.to", TagResolver.resolver(
            Placeholder.parsed("player", receiver.name),
            Placeholder.parsed("message", message)
        )))
        PlayerBuilderAction
            .player(receiver)
            .sound(magenta.config.getString("msg.sound").toString(),
                magenta.config.getString("msg.volume").toString().toFloat(),
                magenta.config.getString("msg.pitch").toString().toFloat()
            ).message(magenta.localeConfig.translation("magenta.command.msg.success.from", TagResolver.resolver(
                Placeholder.parsed("player", commandSender.name),
                Placeholder.parsed("message", message)
            )))
    }

}