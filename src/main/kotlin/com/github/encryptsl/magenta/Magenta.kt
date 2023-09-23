package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.api.KitConfig
import com.github.encryptsl.magenta.api.KitManager
import com.github.encryptsl.magenta.api.config.ConfigLoader
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.CommandManager
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.database.models.HomeModel
import com.github.encryptsl.magenta.common.database.models.WarpModel
import com.github.encryptsl.magenta.common.utils.TeamIntegration
import com.github.encryptsl.magenta.listeners.custom.*
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.measureTimedValue

class Magenta : JavaPlugin() {

    private val commandManager: CommandManager by lazy { CommandManager(this) }
    val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    val teamIntegration: TeamIntegration by lazy { TeamIntegration() }
    val localeConfig: Locale by lazy { Locale(this) }
    val kitConfig: KitConfig by lazy { KitConfig(this) }
    val schedulerMagenta: SchedulerMagenta by lazy { SchedulerMagenta(this) }
    val homeModel: HomeModel by lazy { HomeModel(this) }
    val warpModel: WarpModel by lazy { WarpModel(this) }
    val kitManager: KitManager by lazy { KitManager(this) }
    val pluginManager = server.pluginManager

    override fun onLoad() {
        configLoader
            .createFromResources("locale/cs_cz.yml", this)
            .createFromResources("kits.yml", this)
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

    private fun handlerListener() {
        val list: ArrayList<Listener> = arrayListOf(
            HomeCreateListener(this),
            HomeDeleteListener(this),
            HomeMoveLocationListener(this),
            HomeRenameListener(this),
            HomeTeleportListener(this),
            KitAdminGiveListener(this),
            KitRecieveListener(this),
            TpaDenyListener(this),
            TpaRequestListener(this),
            WarpCreateListener(this),
            WarpDeleteListener(this),
            WarpInfoListener(this),
            WarpMoveLocationListener(this),
            WarpRenameListener(this),
            WarpTeleportListener(this),
        )

        val (value, time) = measureTimedValue {
            if (list.isEmpty()) {
                logger.info("Registering list of listeners is empty !")
            }

            list.forEach {pluginManager.registerEvents(it, this)
                logger.info("Discord adapter ${it.javaClass.simpleName} registered () -> ok")
            }
        }

        logger.info("Bukkit listeners registered (${list.size}) in time ${time.inWholeSeconds}s -> $value")
        list.removeAll(list.toSet())
    }
}