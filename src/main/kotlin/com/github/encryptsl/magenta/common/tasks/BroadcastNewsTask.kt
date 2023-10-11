package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit

class BroadcastNewsTask(private val magenta: Magenta) : Runnable {
    override fun run() {
       if (!magenta.config.contains("news")) return
       if (!magenta.config.contains("news.delay")) return
       if (magenta.config.contains("news.random")) {
            if (magenta.config.getBoolean("news.random")) {
                val format = magenta.config.getString("news.format").toString()
                val messages = magenta.config.getStringList("news.messages")
                if (messages.isEmpty()) return

                Bukkit.broadcast(ModernText.miniModernText(format,
                    Placeholder.parsed("message", messages.random())
                ))
            }
       }
    }
}