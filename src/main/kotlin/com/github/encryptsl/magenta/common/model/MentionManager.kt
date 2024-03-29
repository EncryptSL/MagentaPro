package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.chat.PlayerMentionEvent
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
                        PlayerBuilderAction.player(it).sound(sound, volume, pitch).message(mentionedPlayer(player, "magenta.player.mentioned"))
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
                        for (p in Bukkit.getOnlinePlayers()) {
                            p.sendMessage(mentionedPlayer(p, "magenta.player.mentioned"))
                            p.playSound(p, sound, volume, pitch)
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

    private fun mentionedPlayer(player: Player, key: String): Component {
        return magenta.localeConfig.translation(key, Placeholder.parsed("player", player.name))
    }
}