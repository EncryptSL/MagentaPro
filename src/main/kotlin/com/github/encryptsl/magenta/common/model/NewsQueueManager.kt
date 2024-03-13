package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import java.util.*

class NewsQueueManager(private val magenta: Magenta) {
    val news: Queue<String> = LinkedList()

    fun isQueueEmpty()
        = news.isEmpty()

    fun loadQueue() {
        val newsList = magenta.config.getStringList("news.messages")
        if (newsList.isEmpty()) return
        news.apply { addAll(newsList) }

        magenta.logger.info("News are loaded in count (${news.size})")
    }

    fun enqueue(message: String) {
        news.add(message)
    }

    fun reloadQueue() {
        val newsList = magenta.config.getStringList("news.messages")

        if (newsList.isEmpty()) return

        if (news.isNotEmpty()) { news.clear() } else { news.apply { addAll(newsList) } }
    }

}