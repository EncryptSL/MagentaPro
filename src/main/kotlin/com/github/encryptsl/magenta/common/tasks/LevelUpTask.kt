package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.level.VirtualLevelUpEvent
import com.github.encryptsl.magenta.api.level.LevelFormula
import fr.euphyllia.energie.model.SchedulerCallBack
import fr.euphyllia.energie.model.SchedulerTaskInter
import fr.euphyllia.energie.model.SchedulerType
import org.bukkit.Bukkit

class LevelUpTask(private val magenta: Magenta) : SchedulerCallBack {

    override fun run(e: SchedulerTaskInter?) {
        if (e == null) return
        for (player in Bukkit.getOnlinePlayers()) {
            val (_: String, _: String, level: Int, experience: Int) = magenta.virtualLevel.getLevel(player.uniqueId)
            if (experience >= LevelFormula.experienceFormula(level)) {
                Magenta.scheduler.runTask(SchedulerType.SYNC) {
                    magenta.pluginManager.callEvent(
                        VirtualLevelUpEvent(
                            player,
                            level,
                            experience,
                            LevelFormula.experienceFormula(level.plus(1))
                        )
                    )
                }
            }
        }
    }
}