package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit


class BroadcastNewsTask(private val magenta: Magenta) : Runnable {

    private var timeTask = 0

    override fun run() {
        if (!magenta.config.getBoolean("news.enable", false)) return
        if (!magenta.config.contains("news")) return
        if (magenta.newsQueueManager.isQueueEmpty()) return

        val isRandomEnabled = magenta.config.getBoolean("news.random", false)

        val format = magenta.config.getString("news.format").toString()
        sendActionBar(format, isRandomEnabled)
    }

    private fun getMessage(): String {
       val isRandomEnabled = magenta.config.getBoolean("news.random", false)
        return if (isRandomEnabled) magenta.newsQueueManager.news.random() else magenta.newsQueueManager.news.poll()
    }

    private fun sendActionBar(format: String, isRandomEnable: Boolean) {
        if (magenta.config.getBoolean("news.options.actionbar")) {
            if (isRandomEnable) {
                val durationInSeconds = magenta.config.getInt("news.show_time")
                var currentMessage = getMessage()
                val audience = Audience.audience(Bukkit.getOnlinePlayers()
                    .filter { p -> !magenta.user.getUser(p.uniqueId).isVanished() || !p.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
                if (timeTask >= durationInSeconds) {
                    currentMessage = getMessage()
                    timeTask = 0
                }
                audience.sendActionBar(ModernText.miniModernText(format, Placeholder.parsed("message", currentMessage)))
                timeTask++
            } else {
                val durationInSeconds = magenta.config.getInt("news.show_time")
                var currentMessage = getMessage()
                val audience = Audience.audience(Bukkit.getOnlinePlayers()
                    .filter { p -> !magenta.user.getUser(p.uniqueId).isVanished() || !p.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
                if (timeTask >= durationInSeconds) {
                    currentMessage = getMessage()
                    timeTask = 0
                }
                audience.sendActionBar(ModernText.miniModernText(format, Placeholder.parsed("message", currentMessage)))
                magenta.newsQueueManager.enqueue(currentMessage)
                timeTask++
            }
        }
    }
}