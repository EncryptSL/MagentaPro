package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.api.config.ConfigLoader
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.scheduler.AbstractSchedulerMagenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.TeamIntegration
import org.bukkit.plugin.java.JavaPlugin

class Magenta : JavaPlugin() {

    val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    val teamIntegration: TeamIntegration by lazy { TeamIntegration() }
    val localeConfig: Locale by lazy { Locale(this) }
    val schedulerMagenta: SchedulerMagenta by lazy { SchedulerMagenta(this) }

    override fun onLoad() {
        configLoader
            .createFromResources("locale/cs_cz.yml", this)
            .createFromResources("config.yml", this)
        localeConfig.loadLocale("locale/cs_cz.properties")
    }

    override fun onEnable() {
        val start = System.currentTimeMillis()
        teamIntegration.createTeams()
        logger.info("Plugin enabled in time ${start - System.currentTimeMillis()}")
    }



    override fun onDisable() {
        super.onDisable()
    }
}