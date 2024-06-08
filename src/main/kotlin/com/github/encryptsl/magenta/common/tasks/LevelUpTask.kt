package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.extensions.experienceFormula
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.VirtualLevelUpEvent
import org.bukkit.Bukkit

class LevelUpTask(private val magenta: Magenta) : Runnable {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(player.uniqueId)
            if (experience >= experienceFormula(level)) {
                magenta.pluginManager.callEvent(VirtualLevelUpEvent(player, level, experience, experienceFormula(level.plus(1))))
            }
        }
    }
}