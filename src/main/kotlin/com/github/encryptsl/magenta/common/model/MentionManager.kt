package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.chat.PlayerMentionEvent
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit

class MentionManager(private val magenta: Magenta) {

    fun mentionProcess(chatEvent: AsyncChatEvent) {
        val player = chatEvent.player
        val message = PlainTextComponentSerializer.plainText().serialize(chatEvent.message())
        if (message.contains(magenta.config.getString("mentions.variable").toString())) {
            val split = message.split(" ")
            for (m in split) {
                if (m.contains(magenta.config.getString("mentions.variable").toString())) {
                    val sound: String = magenta.config.getString("mentions.sound").toString()
                    val volume = magenta.config.getString("mentions.volume").toString().toFloat()
                    val pitch = magenta.config.getString("mentions.pitch").toString().toFloat()
                    val mentionedMessage = magenta.localeConfig.getMessage("magenta.player.mentioned")

                    SchedulerMagenta.doAsync(magenta) {
                        Bukkit.getPlayer(m.replace("@", ""))?.let {
                            SchedulerMagenta.doSync(magenta) {
                                magenta.pluginManager.callEvent(PlayerMentionEvent(player, it))
                            }
                        }
                    }
                    val mentioned =
                        Bukkit.getPlayer(m.replace(magenta.config.getString("mentions.variable").toString(), ""))

                    mentioned?.let {
                        PlayerBuilderAction.player(it).sound(sound, volume, pitch).message(
                            ModernText.miniModernText(mentionedMessage, TagResolver.resolver(
                                    Placeholder.parsed("player", player.name)
                                )
                            )
                        )
                        chatEvent.message(
                            ModernText.miniModernText(
                                message.replace(
                                    m, magenta.config.getString("mentions.formats.player").toString()
                                        .replace("[player]", mentioned.name)
                                )
                            )
                        )
                    }
                    if (m.contains("@everyone")) {
                        Bukkit.getServer().onlinePlayers.forEach { a ->
                            //if (player == a) return
                            a.sendMessage(
                                ModernText.miniModernText(mentionedMessage, TagResolver.resolver(
                                        Placeholder.parsed("player", a.name)
                                    )
                                )
                            )
                            a.playSound(a, sound, volume, pitch)
                        }
                        chatEvent.message(
                            ModernText.miniModernText(
                                message.replace(
                                    m,
                                    magenta.config.getString("mentions.formats.everyone").toString()
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}