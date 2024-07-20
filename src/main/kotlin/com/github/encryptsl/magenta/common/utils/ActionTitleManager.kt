package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import java.time.Duration
import java.util.concurrent.TimeUnit

object ActionTitleManager {

    fun sendTitleAndSubtitle(audience: Audience, title: Component, subtitle: Component, fadeIn: Long, stay: Long, fadeOut: Long) {
        audience.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut)))
        sendTitle(audience, title)
        sendSubtitle(audience, subtitle)
    }

    fun sendTitleAndSubtitle(audience: Audience, title: Component, subtitle: Component) {
        sendTitle(audience, title)
        sendSubtitle(audience, subtitle)
    }

    fun sendTitle(audience: Audience, title: Component) {
        audience.sendTitlePart(TitlePart.TITLE, title)
    }

    fun sendSubtitle(audience: Audience, subtitle: Component) {
        audience.sendTitlePart(TitlePart.SUBTITLE, subtitle)
    }

    fun sendActionBar(audience: Audience, component: Component) {
        audience.sendActionBar(component)
    }

    fun sendTimedActionBar(audience: Audience, component: Component, durationInSeconds: Int) {
        // Repeating task to keep sending the message
        val task = Magenta.scheduler.impl.runTimer(Runnable {
            audience.sendActionBar(component)
        }, 0L, durationInSeconds.toLong(), TimeUnit.SECONDS)

        Magenta.scheduler.impl.runLater(Runnable {
            task.cancel()
        }, durationInSeconds.toLong(), TimeUnit.SECONDS)
    }
}