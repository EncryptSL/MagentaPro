package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.api.config.ConfigLoader
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.CommandManager
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.database.models.HomeModel
import com.github.encryptsl.magenta.common.database.models.WarpModel
import com.github.encryptsl.magenta.common.utils.TeamIntegration
import org.bukkit.plugin.java.JavaPlugin

class Magenta : JavaPlugin() {

    private val commandManager: CommandManager by lazy { CommandManager(this) }
    val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    val teamIntegration: TeamIntegration by lazy { TeamIntegration() }
    val localeConfig: Locale by lazy { Locale(this) }
    val schedulerMagenta: SchedulerMagenta by lazy { SchedulerMagenta(this) }
    val homeModel: HomeModel by lazy { HomeModel() }
    val warpModel: WarpModel by lazy { WarpModel() }

    override fun onLoad() {
        configLoader
            .createFromResources("locale/cs_cz.yml", this)
            .createFromResources("config.yml", this)
        localeConfig.loadLocale("locale/cs_cz.properties")
        DatabaseConnector().connect(
            config.getString("database.host") ?: "jdbc:sqlite:plugins/LiteEco/database.db",
            config.getString("database.username") ?: "root",
            config.getString("database.password") ?: "admin"
        )
    }

    override fun onEnable() {
        val start = System.currentTimeMillis()
        teamIntegration.createTeams()
        commandManager.registerCommands()
        logger.info("Plugin enabled in time ${start - System.currentTimeMillis()}")
    }



    override fun onDisable() {
        super.onDisable()
    }
}