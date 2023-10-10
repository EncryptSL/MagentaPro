package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import kotlin.random.Random

class BroadcastNewsTask(private val magenta: Magenta) : Runnable {
    override fun run() {
       if (!magenta.config.contains("news")) return
       if (!magenta.config.contains("news.delay")) return
       if (magenta.config.contains("news.random")) {
            if (magenta.config.getBoolean("news.random")) {
                val messages = magenta.config.getStringList("news.messages")
                val randomInt = Random.nextInt(messages.size)
                val randomMessage = messages[randomInt]
                Bukkit.broadcast(ModernText.miniModernText(randomMessage,
                    Placeholder.parsed("prefix", magenta.config.getString("news.prefix").toString())
                ))
            }
       }
    }
}