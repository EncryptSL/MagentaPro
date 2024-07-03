package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.chat.PlayerMentionEvent
import com.github.encryptsl.magenta.common.PlayerBuilderAction
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
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

                    if (m.contains("@everyone")) {
                        for (p in Bukkit.getOnlinePlayers()) {
                            p.sendMessage(mentionedPlayer(p, "magenta.player.mentioned"))
                            p.playSound(Sound.sound().volume(volume).pitch(pitch).type(Key.key(sound)).build())
                        }
                    }

                    val mentioned = Bukkit.getPlayer(m.replace(magenta.config.getString("mentions.variable").toString(), "")) ?: return

                    Magenta.scheduler.impl.runNextTick {
                        magenta.pluginManager.callEvent(PlayerMentionEvent(player, mentioned))
                    }
                    PlayerBuilderAction.player(mentioned).sound(sound, volume, pitch).message(mentionedPlayer(player, "magenta.player.mentioned"))
                    chatEvent.message(
                        ModernText.miniModernText(message.replace(
                            m, magenta.config.getString("mentions.formats.player").toString().replace("[player]", magenta.config.getString("mentions.variable").toString() + mentioned.name.lowercase())
                        ))
                    )
                }
            }
        }
    }

    private fun mentionedPlayer(player: Player, key: String): Component {
        return magenta.locale.translation(key, Placeholder.parsed("player", player.name))
    }
}