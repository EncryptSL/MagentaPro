package com.github.encryptsl.magenta.api.webhook

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder


class DiscordWebhook(url: String) {
    private val webhookBuilder = WebhookClientBuilder(url)
    var client: WebhookClient
    init {
        webhookBuilder.setThreadFactory { job ->
            val thread = Thread(job)
            thread.setName("DiscordWebhook")
            thread.setDaemon(true)
            thread
        }
        webhookBuilder.setWait(true)
        client = webhookBuilder.build()
    }
    fun addEmbed(embedBuilder: WebhookEmbedBuilder.() -> Unit): WebhookEmbed
    {
        return WebhookEmbedBuilder().apply(embedBuilder).build()
    }
}