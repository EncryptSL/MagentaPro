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
                val randomMessage = magenta.config.getStringList("news.messages").random()
                Bukkit.broadcast(ModernText.miniModernText(randomMessage,
                    Placeholder.parsed("prefix", magenta.config.getString("news.prefix").toString())
                ))
            }
       }
    }
}