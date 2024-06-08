package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import java.util.*

class BroadcastNewsTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        if (!magenta.config.contains("news")) return
        if (magenta.newsQueueManager.isQueueEmpty()) return

        val isRandomEnabled = magenta.config.getBoolean("news.random", false)
        runSender(magenta.newsQueueManager.news, isRandomEnabled)
    }

    private fun runSender(messages: Queue<String>, isRandomEnabled: Boolean) {
        val format = magenta.config.getString("news.format").toString()
        if (isRandomEnabled) {
            val randomMessage = messages.random()
            sendMessage(format, randomMessage)
        } else {
            val poolMessage = messages.poll()
            sendMessage(format, poolMessage)
            magenta.newsQueueManager.enqueue(poolMessage)
        }
    }

    private fun sendMessage(format: String, message: String) {
        if (magenta.config.getBoolean("news.options.actionbar")) {
            Audience.audience(Bukkit.getOnlinePlayers())
                .sendActionBar(ModernText.miniModernText(format, Placeholder.parsed("message", message)))
        }

        if (magenta.config.getBoolean("news.options.broadcast")) {
            Audience.audience(Bukkit.getOnlinePlayers()).sendMessage(
                ModernText.miniModernText(
                format, Placeholder.parsed("message", message)
            ))
        }
    }
}