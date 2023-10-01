package com.github.encryptsl.magenta.api

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.chat.PlayerMentionEvent
import com.github.encryptsl.magenta.common.utils.ModernText
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Sound

class MentionManager(private val magenta: Magenta) {

    fun mentionProcess(chatEvent: AsyncChatEvent) {
        val player = chatEvent.player
        val message = PlainTextComponentSerializer.plainText().serialize(chatEvent.message())
        if (message.contains(magenta.config.getString("mentions.variable").toString())) {
            val split = message.split(" ")
            for (m in split) {
                if (m.contains(magenta.config.getString("mentions.variable").toString())) {
                    magenta.schedulerMagenta.runTask(magenta) {
                        Bukkit.getPlayer(m.replace("@", ""))?.let {
                            magenta.pluginManager.callEvent(PlayerMentionEvent(player, it))
                        }
                    }
                    val mentioned = Bukkit.getPlayer(m.replace(magenta.config.getString("mentions.variable").toString(), ""))
                    mentioned?.playSound(mentioned,
                        Sound.valueOf(magenta.config.getString("mentions.sound").toString()),
                        magenta.config.getString("mentions.volume").toString().toFloat(),
                        magenta.config.getString("mentions.pitch").toString().toFloat()
                    )
                    mentioned?.sendMessage(ModernText.miniModernText("<color:#5aa2fa>Jsi zmíněn hráčem ${player.name}"))
                    if (mentioned != null) {
                        chatEvent.message(
                            ModernText.miniModernText(message.replace(
                            m,
                            magenta.config.getString("mentions.formats.player").toString()
                                .replace("[player]", m)
                        )))
                    }
                }
                if (m.contains("@everyone")) {
                    Bukkit.getServer().onlinePlayers.forEach { a ->
                        //if (player == a) return
                        a.sendMessage(
                            ModernText.miniModernText(
                                magenta.localeConfig.getMessage("magenta.player.mentioned"), TagResolver.resolver(
                                    Placeholder.parsed("player", a.name)
                                )
                            )
                        )
                        a.playSound(
                            a,
                            Sound.valueOf(magenta.config.getString("mentions.sound").toString()),
                            magenta.config.getString("mentions.volume").toString().toFloat(),
                            magenta.config.getString("mentions.pitch").toString().toFloat()
                        )
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