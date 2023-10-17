package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.VirtualLevelUpEvent
import com.github.encryptsl.magenta.api.level.LevelFormula

class LevelUpTask(private val magenta: Magenta) : Runnable {
    override fun run() {
        magenta.server.onlinePlayers.forEach { player ->
            val playerData = magenta.virtualLevel.getLevel(player.uniqueId)
            if (playerData.experience >= LevelFormula.experienceFormula(playerData.level)) {
                magenta.pluginManager.callEvent(VirtualLevelUpEvent(
                    player,
                    playerData.level,
                    playerData.experience,
                    LevelFormula.experienceFormula(playerData.level.plus(1))
                ))
            }
        }
    }
}