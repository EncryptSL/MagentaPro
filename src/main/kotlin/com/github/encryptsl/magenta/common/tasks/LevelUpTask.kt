package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.VirtualLevelUpEvent
import com.github.encryptsl.magenta.api.level.LevelFormula

class LevelUpTask(private val magenta: Magenta) : Runnable {
    override fun run() {
        magenta.server.onlinePlayers.forEach { player ->
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(player.uniqueId)
            if (experience >= LevelFormula.experienceFormula(level)) {
                magenta.schedulerMagenta.doSync(magenta) {
                    magenta.pluginManager.callEvent(VirtualLevelUpEvent(
                        player,
                        level,
                        experience,
                        LevelFormula.experienceFormula(level.plus(1))
                    ))
                }
            }
        }
    }
}