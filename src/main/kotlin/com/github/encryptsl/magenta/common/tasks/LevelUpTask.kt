package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.extensions.experienceFormula
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.LevelUpEvent
import org.bukkit.Bukkit

class LevelUpTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            magenta.levelAPI.getUserByUUID(player.uniqueId).thenAccept {
                if (it.experience >= experienceFormula(it.level)) {
                    magenta.pluginManager.callEvent(LevelUpEvent(player, it.level, it.experience, experienceFormula(it.level.plus(1))))
                }
            }
        }
    }
}