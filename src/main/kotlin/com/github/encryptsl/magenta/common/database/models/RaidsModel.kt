package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.sql.RaidsSQL
import org.bukkit.Location

class RaidsModel(private val magenta: Magenta) : RaidsSQL {
    override fun createArena(bossName: String, location: Location) {
        magenta.schedulerMagenta.doAsync(magenta) {
            
        }
    }

    override fun setName(bossName: String, newBossName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {

        }
    }

    override fun setPrice(bossName: String, price: Int) {
        magenta.schedulerMagenta.doAsync(magenta) {

        }
    }

    override fun moveArena(bossName: String, location: Location) {
        magenta.schedulerMagenta.doAsync(magenta) {

        }
    }

    override fun deleteArena(bossName: String) {
        magenta.schedulerMagenta.doAsync(magenta) {

        }
    }
}