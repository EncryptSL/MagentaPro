package com.github.encryptsl.magenta.api.webhook

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder


class DiscordWebhook(private val url: String) {

    lateinit var client: WebhookClient

    init {
        createClient()
    }

    private fun createClient() {
        try {
           client = WebhookClientBuilder(url).setThreadFactory { job ->
                 val thread = Thread(job)
                 thread.setName("DiscordWebhook")
                 thread.setDaemon(true)
                 thread
             }.setWait(true).build()
        } catch (e : Exception) { client.close() }
    }

    fun addEmbed(embedBuilder: WebhookEmbedBuilder.() -> Unit): WebhookEmbed?
    {
        try {
            return WebhookEmbedBuilder().apply(embedBuilder).build()
        } catch (e : Exception) {
            e.fillInStackTrace()
        }

        return null
    }
}