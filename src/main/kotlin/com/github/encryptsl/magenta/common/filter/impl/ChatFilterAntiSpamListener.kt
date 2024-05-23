package com.github.encryptsl.magenta.common.filter.impl

import com.github.encryptsl.magenta.Magenta
import com.google.common.cache.RemovalCause
import com.google.common.cache.RemovalListener
import com.google.common.cache.RemovalNotification

class ChatFilterAntiSpamListener<K, V> : RemovalListener<K, V> {

    override fun onRemoval(notification: RemovalNotification<K, V>) {
        val k = notification.key
        val v = notification.value

        val cause = notification.cause

        when (cause) {
            RemovalCause.EXPIRED -> {
                Magenta.instance.logger.info("${this.javaClass.simpleName} cache expired cache from uuid: $k")
            }
            else -> null
        }
    }

}