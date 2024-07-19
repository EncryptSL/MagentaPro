package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit

class ActionBarNewsTask(private val magenta: Magenta) : Runnable {

    private var timeTask: Int = 0
    private var currentMessage: String = magenta.newsQueueManager.news.random()

    override fun run() {
        if (!magenta.config.getBoolean("news.enable", false)) return
        if (!magenta.config.contains("news")) return
        if (magenta.newsQueueManager.isQueueEmpty()) return

        val isRandomEnabled = magenta.config.getBoolean("news.random", false)

        val format = magenta.config.getString("news.format").toString()
        if (magenta.config.getBoolean("news.options.actionbar")) {
            if (isRandomEnabled) {
                val durationInSeconds = magenta.config.getInt("news.show_time")
                val audience = Audience.audience(
                    Bukkit.getOnlinePlayers()
                        .filter { p -> !magenta.user.getUser(p.uniqueId).isVanished() || !p.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
                if (timeTask >= durationInSeconds) {
                    currentMessage = magenta.newsQueueManager.news.random()
                    timeTask = 0
                }
                audience.sendActionBar(ModernText.miniModernText(format, Placeholder.parsed("message", currentMessage)))
                timeTask++
            } else {
                val durationInSeconds = magenta.config.getInt("news.show_time")
                var currentMessage = magenta.newsQueueManager.news.poll()
                val audience = Audience.audience(
                    Bukkit.getOnlinePlayers()
                        .filter { p -> !magenta.user.getUser(p.uniqueId).isVanished() || !p.hasPermission(Permissions.NEWS_VISIBLE_EXEMPT) })
                if (timeTask >= durationInSeconds) {
                    currentMessage = magenta.newsQueueManager.news.poll()
                    timeTask = 0
                }
                audience.sendActionBar(ModernText.miniModernText(format, Placeholder.parsed("message", currentMessage)))
                magenta.newsQueueManager.enqueue(currentMessage)
                timeTask++
            }
        }

    }
}