package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.extensions.experienceFormula
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.VirtualLevelUpEvent
import org.bukkit.Bukkit

class LevelUpTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            magenta.virtualLevel.getUserByUUID(player.uniqueId).thenAccept {
                if (it.experience >= experienceFormula(it.level)) {
                    magenta.pluginManager.callEvent(VirtualLevelUpEvent(player, it.level, it.experience, experienceFormula(it.level.plus(1))))
                }
            }
        }
    }
}