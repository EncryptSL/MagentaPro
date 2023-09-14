package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.AbstractChatFilter
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.Algorithms
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.*
import java.util.concurrent.TimeUnit


class AntiSpam(val magenta: Magenta, private val violations: Violations) : AbstractChatFilter(magenta, violations) {

    private val spam: MutableMap<UUID, String> = HashMap()

    private val algorithms = Algorithms()

    override fun detection(event: AsyncChatEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (!magenta.config.getBoolean("chat.filters.${violations.name}.control")) return

        if (!spam.containsKey(uuid) || !spam.get(uuid).equals(message, true))
            spam[uuid] = message

        if (algorithms.checkSimilarity(message, spam[uuid].toString()) > magenta.config.getInt("chat.filters.antispam.similarity")) {
            filterManager().action(player, event, magenta.locale.getMessage("magenta.filter.antispam"), null, null)
            magenta.server.asyncScheduler.runDelayed(magenta, {
                spam.remove(uuid)
            }, 5, TimeUnit.SECONDS)
            event.isCancelled = true
        }
    }


}