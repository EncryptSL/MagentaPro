package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPardonEvent
import com.github.encryptsl.magenta.common.extensions.formatFromSecondsTime
import fr.euphyllia.energie.model.SchedulerCallBack
import fr.euphyllia.energie.model.SchedulerTaskInter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Sound

class JailCountDownTask(private val magenta: Magenta) : SchedulerCallBack {
    override fun run(e: SchedulerTaskInter?) {
        if (e == null) return
        for (player in e.plugin.server.onlinePlayers) {
            val account = magenta.user.getUser(player.uniqueId)
            val timeLeft = account.getRemainingJailTime()
            if (account.hasPunish()) {
                if (timeLeft == 0L) {
                    magenta.pluginManager.callEvent(JailPardonEvent(player))
                }
                account.setOnlineTime(timeLeft)
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.15f, 1.15f)
                player.sendActionBar(magenta.localeConfig.translation("magenta.command.jail.success.remaining",
                    Placeholder.parsed("remaining", formatFromSecondsTime(timeLeft))
                ))
            }
        }
    }
}