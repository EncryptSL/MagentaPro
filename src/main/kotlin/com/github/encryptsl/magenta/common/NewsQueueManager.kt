package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import java.util.*

class NewsQueueManager(private val magenta: Magenta) {
    val news: Queue<String> = LinkedList()

    fun loadQueue() {
        val newsList = magenta.config.getStringList("news.messages")
        if (newsList.isEmpty()) return
        news.addAll(newsList)

        magenta.logger.info("News are loaded in count (${news.size})")
        magenta.logger.info("News available $news")
    }

    fun reloadQueue() {
        val newsList = magenta.config.getStringList("news.messages")

        if (newsList.isEmpty()) return

        if (news.isNotEmpty()) { news.clear() } else { news.addAll(newsList) }
    }

}