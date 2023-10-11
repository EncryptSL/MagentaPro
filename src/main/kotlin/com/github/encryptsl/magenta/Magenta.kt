package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.api.*
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.api.config.ConfigLoader
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.api.votes.VotePlayerAPI
import com.github.encryptsl.magenta.common.CommandManager
import com.github.encryptsl.magenta.common.TpaManager
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.database.models.HomeModel
import com.github.encryptsl.magenta.common.database.models.WarpModel
import com.github.encryptsl.magenta.common.filter.modules.*
import com.github.encryptsl.magenta.common.hook.HookManager
import com.github.encryptsl.magenta.common.tasks.BroadcastNewsTask
import com.github.encryptsl.magenta.common.tasks.JailCountDownTask
import com.github.encryptsl.magenta.common.tasks.PlayerAfkTask
import com.github.encryptsl.magenta.common.utils.AfkUtils
import com.github.encryptsl.magenta.common.utils.StringUtils
import com.github.encryptsl.magenta.common.utils.TeamIntegration
import com.github.encryptsl.magenta.listeners.*
import com.github.encryptsl.magenta.listeners.custom.*
import io.papermc.paper.util.Tick
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class Magenta : JavaPlugin() {

    val pluginManager = server.pluginManager
    val vote: VotePlayerAPI by lazy { VotePlayerAPI(this) }
    val stringUtils: StringUtils by lazy { StringUtils(this) }
    val teamIntegration: TeamIntegration by lazy { TeamIntegration(this) }
    val schedulerMagenta: SchedulerMagenta by lazy { SchedulerMagenta() }
    val homeModel: HomeModel by lazy { HomeModel(this) }
    val warpModel: WarpModel by lazy { WarpModel(this) }
    val kitManager: KitManager by lazy { KitManager(this) }
    val tpaManager: TpaManager by lazy { TpaManager(this) }
    val afk: AfkUtils by lazy { AfkUtils(this) }

    val localeConfig: Locale by lazy { Locale(this) }
    val kitConfig: KitConfig by lazy { KitConfig(this) }
    val jailConfig: JailConfig by lazy { JailConfig(this) }
    val cItems: CommandItemConfig by lazy { CommandItemConfig(this) }
    val tags: TagsConfig by lazy { TagsConfig(this) }

    private val commandManager: CommandManager by lazy { CommandManager(this) }
    private val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    private val hookManger: HookManager by lazy { HookManager(this) }

    override fun onLoad() {
        configLoader
            .createFromResources("locale/cs_cz.properties", this)
            .createFromResources("kits.yml", this)
            .createFromResources("config.yml", this)
            .createFromResources("swear_list.txt", this)
            .createFromResources("motd.txt", this)
            .createFromResources("citems.yml", this)
            .createFromResources("tags.yml", this)
            .create("jails.yml")
        localeConfig.loadLocale("locale/cs_cz.properties")
        DatabaseConnector().initConnect(
            config.getString("database.host") ?: "jdbc:sqlite:plugins/MagentaPro/database.db",
            config.getString("database.username") ?: "root",
            config.getString("database.password") ?: "admin"
        )
    }

    override fun onEnable() {
        val time = measureTime {
            teamIntegration.createTeams()
            commandManager.registerCommands()
            registerTasks()
            handlerListener()
            hookRegistration()
        }
        logger.info("Plugin enabled in time ${time.inWholeSeconds}")
    }



    override fun onDisable() {
        logger.info("Plugin disabled")
    }

    private fun hookRegistration() {
        hookManger.hookPAPI()
        hookManger.hookNuVotifier()
    }

    private fun registerTasks() {
        schedulerMagenta.runTaskTimerAsyncTask(this, BroadcastNewsTask(this), Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong(), Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong())
        schedulerMagenta.runTaskTimerAsync(this, PlayerAfkTask(this), 20L, 20)
        schedulerMagenta.runTaskTimerAsync(this, JailCountDownTask(this), 20, 20)
    }

    private fun handlerListener() {
        val list: ArrayList<Listener> = arrayListOf(
            AsyncChatListener(this),
            AsyncFilterChat(AntiSpam(this, Violations.ANTISPAM)),
            AsyncFilterChat(CapsLock(this, Violations.CAPSLOCK)),
            AsyncFilterChat(IPFilter(this, Violations.IPFILTER)),
            AsyncFilterChat(Swear(this, Violations.SWEAR)),
            AsyncFilterChat(WebsiteFilter(this, Violations.WEBSITE)),
            EntityAttackListener(this),
            BlockListener(this),
            PlayerAsyncLogin(this),
            PlayerJoinListener(this),
            PlayerQuitListener(this),
            PlayerTeleportListener(this),
            PlayerIgnoreListener(this),
            PlayerCommandPreprocessListener(this),
            PlayerPrivateMessageListener(this),
            PlayerInteractionListener(this),
            PlayerMoveListener(this),
            HomeCreateListener(this),
            HomeDeleteListener(this),
            HomeInfoListener(this),
            HomeMoveLocationListener(this),
            HomeRenameListener(this),
            HomeTeleportListener(this),
            JailInfoListener(this),
            JailCheckListener(this),
            JailCreateListener(this),
            JailDeleteListener(this),
            JailListener(this),
            JailPlayerListener(this),
            JailPardonListener(this),
            JailTeleportListener(this),
            KitCreateListener(this),
            KitDeleteListener(this),
            KitGiveListener(this),
            KitInfoListener(this),
            KitReceiveListener(this),
            SocialSpyListener(this),
            TpaListener(this),
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
                logger.info("Listener ${it.javaClass.simpleName} registered () -> ok")
            }
        }

        logger.info("Bukkit listeners registered (${list.size}) in time $time -> $value")
        list.removeAll(list.toSet())
    }
}