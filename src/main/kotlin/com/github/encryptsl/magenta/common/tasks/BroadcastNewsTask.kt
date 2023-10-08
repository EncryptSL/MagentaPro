package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.Bukkit

class BroadcastNewsTask(private val magenta: Magenta) : Runnable {
    override fun run() {
       if (!magenta.config.contains("news")) return
       if (!magenta.config.contains("news.delay")) return
       if (magenta.config.contains("news.random")) {
            if (magenta.config.getBoolean("true")) {
                val randomMessage = magenta.config.getStringList("news.messages").random()
                Bukkit.broadcast(ModernText.miniModernText(randomMessage))
                return
            } else {
                val messages = magenta.config.getStringList("news.messages").size
                var current = messages
                if (current == 0) {
                    current = messages
                }
                current--
                Bukkit.broadcast(ModernText.miniModernText(magenta.config.getStringList("news.messagess").elementAt(current)))
            }
       }
    }
}