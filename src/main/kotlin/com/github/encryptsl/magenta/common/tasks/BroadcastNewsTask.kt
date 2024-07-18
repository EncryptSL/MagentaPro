package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit


class BroadcastNewsTask(private val magenta: Magenta) {

    fun run() {
        if (!magenta.config.getBoolean("news.enable", false)) return
        if (!magenta.config.contains("news")) return
        if (magenta.newsQueueManager.isQueueEmpty()) return

        val isRandomEnabled = magenta.config.getBoolean("news.random", false)

        val format = magenta.config.getString("news.format").toString()
        when(isRandomEnabled) {
            true -> {
                sendBroadcastMessage(format, magenta.newsQueueManager.news.random())
                sendActionBar(format)
            }
            false -> {
                val poolMessage = magenta.newsQueueManager.news.poll()
                sendBroadcastMessage(format, poolMessage)
                magenta.newsQueueManager.enqueue(poolMessage)
            }
        }
    }

    private fun sendActionBar(format: String) {
        if (magenta.config.getBoolean("news.options.actionbar")) {
            val durationInSeconds = magenta.config.getInt("news.show_time")

            val audience = Audience.audience(Bukkit.getOnlinePlayers()
                .filter { p -> !magenta.user.getUser(p.uniqueId).isVanished() || !p.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })

           Magenta.scheduler.impl.runTimer(Runnable {
                audience.sendActionBar(ModernText.miniModernText(format, Placeholder.parsed("message", magenta.newsQueueManager.news.random())))
           }, 0L, durationInSeconds.toLong(), TimeUnit.SECONDS)
        }
    }

    private fun sendBroadcastMessage(format: String, message: String) {
        if (magenta.config.getBoolean("news.options.broadcast")) {
            Audience.audience(Bukkit.getOnlinePlayers().filter { !it.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
                .sendMessage(ModernText.miniModernText(format, Placeholder.parsed("message", message)))
        }
    }
}