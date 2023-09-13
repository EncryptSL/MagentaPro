package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.common.config.ConfigLoader
import com.github.encryptsl.magenta.common.config.locale.Locale
import com.github.encryptsl.magenta.common.utils.TeamIntegration
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.util.Properties
import java.util.logging.Level

class Magenta : JavaPlugin() {

    val locale = Locale(this)
    val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    val teamIntegration: TeamIntegration by lazy { TeamIntegration() }
    val localeConfig: Locale by lazy { Locale(this) }

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