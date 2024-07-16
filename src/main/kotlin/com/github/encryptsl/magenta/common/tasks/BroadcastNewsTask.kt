package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import java.util.*


class BroadcastNewsTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        if (!magenta.config.getBoolean("news.enable", false)) return
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
            val audience = Audience.audience(Bukkit.getOnlinePlayers()
                .filter { p -> !magenta.user.getUser(p.uniqueId).isVanished() || ! p.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
            sendTimedActionBar(
                audience,
                ModernText.miniModernText(format, Placeholder.parsed("message", message)), magenta.config.getInt("news.show_time")
            )
        }
        if (magenta.config.getBoolean("news.options.broadcast")) {
            Audience.audience(Bukkit.getOnlinePlayers().filter { !it.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
                .sendMessage(ModernText.miniModernText(format, Placeholder.parsed("message", message)))
        }
    }

    private fun sendTimedActionBar(audience: Audience, component: Component, durationInSeconds: Int) {
        val ticks = durationInSeconds * 20

        audience.sendActionBar(component)

        // Repeating task to keep sending the message
        val taskId = Magenta.scheduler.impl.runTimer(Runnable {
            audience.sendActionBar(component)
        }, ticks.toLong(), ticks.toLong())

        Magenta.scheduler.impl.runLater(Runnable {
            taskId.cancel()
        }, ticks.toLong())
    }
}