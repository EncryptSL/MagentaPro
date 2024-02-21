package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.api.ItemFactory
import com.github.encryptsl.magenta.api.account.User
import com.github.encryptsl.magenta.api.config.*
import com.github.encryptsl.magenta.api.config.loader.ConfigLoader
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.containers.PaperContainerProvider
import com.github.encryptsl.magenta.api.level.VirtualLevelAPI
import com.github.encryptsl.magenta.api.manager.JailManager
import com.github.encryptsl.magenta.api.manager.KitManager
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.api.votes.MagentaVoteAPI
import com.github.encryptsl.magenta.api.votes.MagentaVotePartyAPI
import com.github.encryptsl.magenta.api.webhook.DiscordWebhook
import com.github.encryptsl.magenta.common.CommandManager
import com.github.encryptsl.magenta.common.TpaManager
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.database.models.HomeModel
import com.github.encryptsl.magenta.common.database.models.LevelModel
import com.github.encryptsl.magenta.common.database.models.VotePartyModel
import com.github.encryptsl.magenta.common.database.models.WarpModel
import com.github.encryptsl.magenta.common.hook.HookManager
import com.github.encryptsl.magenta.common.tasks.BroadcastNewsTask
import com.github.encryptsl.magenta.common.tasks.JailCountDownTask
import com.github.encryptsl.magenta.common.tasks.LevelUpTask
import com.github.encryptsl.magenta.common.utils.AfkUtils
import com.github.encryptsl.magenta.common.utils.StringUtils
import com.github.encryptsl.magenta.listeners.*
import com.github.encryptsl.magenta.listeners.custom.*
import io.papermc.paper.util.Tick
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

open class Magenta : JavaPlugin() {


    lateinit var paperContainerProvider: PaperContainerProvider
    var random = ThreadLocalRandom.current().nextInt(1000, 9999)
    val pluginManager = server.pluginManager
    val user: User by lazy { User(this) }
    val vote: MagentaVoteAPI by lazy { MagentaVoteAPI(this) }
    val voteParty: MagentaVotePartyAPI by lazy { MagentaVotePartyAPI(VotePartyModel(this)) }
    val virtualLevel: VirtualLevelAPI by lazy { VirtualLevelAPI(this) }
    val stringUtils: StringUtils by lazy { StringUtils(this) }
    val homeModel: HomeModel by lazy { HomeModel(this) }
    val warpModel: WarpModel by lazy { WarpModel(this) }
    val levelModel: LevelModel by lazy { LevelModel(this) }
    val kitManager: KitManager by lazy { KitManager(this) }
    val tpaManager: TpaManager by lazy { TpaManager(this) }
    val afk: AfkUtils by lazy { AfkUtils(this) }
    val itemFactory: ItemFactory by lazy { ItemFactory() }

    val localeConfig: Locale by lazy { Locale(this) }
    val kitConfig: KitConfig by lazy { KitConfig(this) }
    val jailConfig: UniversalConfig by lazy { UniversalConfig(this, "jails.yml") }
    val mmConfig: MMConfig by lazy { MMConfig(this) }
    val cItems: CommandItemConfig by lazy { CommandItemConfig(this) }
    val randomConfig: RandomConfig by lazy { RandomConfig(this) }
    val shopConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/shop/shop.yml") }
    val creditShopConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/creditshop/shop.yml") }
    val oresMenuConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/levels/ores.yml") }
    val homeMenuConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/home/config.yml") }
    val homeEditorConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/home/editor/home_editor.yml") }
    val warpMenuConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/warp/config.yml") }
    val warpPlayerMenuConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/warp/owner_warps.yml") }
    val warpEditorConfig: UniversalConfig by lazy { UniversalConfig(this, "menu/warp/editor/warp_editor.yml") }
    val chatControl: ChatControlConfig by lazy { ChatControlConfig(this) }
    val serverFeedback: DiscordWebhook by lazy { DiscordWebhook(config.getString("discord.webhooks.server_feedback").toString()) }
    val notification: DiscordWebhook by lazy { DiscordWebhook(config.getString("discord.webhooks.notifications").toString()) }
    val jailManager: JailManager by lazy { JailManager(jailConfig.getConfig()) }

    private val commandManager: CommandManager by lazy { CommandManager(this) }
    private val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    private val hookManger: HookManager by lazy { HookManager(this) }

    override fun onLoad() {
        configLoader
            .createFromResources("locale/cs_cz.properties", this)
            .createFromResources("kits.yml", this)
            .createFromResources("config.yml", this)
            .createFromResources("chatcontrol/swears.txt", this)
            .createFromResources("chatcontrol/filter.yml", this)
            .createFromResources("motd.txt", this)
            .createFromResources("citems.yml", this)
            .createFromResources("random.yml", this)
            .createFromResources("mythicmobs/config.yml", this)
            .createFromResources("menu/shop/shop.yml", this)
            .createFromResources("menu/creditshop/shop.yml", this)
            .createFromResources("menu/creditshop/categories/galaxy_box_keys.yml", this)
            .createFromResources("menu/shop/categories/blocks.yml", this)
            .createFromResources("menu/shop/categories/decoration.yml", this)
            .createFromResources("menu/shop/categories/farms.yml", this)
            .createFromResources("menu/shop/categories/food.yml", this)
            .createFromResources("menu/shop/categories/materials.yml", this)
            .createFromResources("menu/shop/categories/ores.yml", this)
            .createFromResources("menu/shop/categories/redstones.yml", this)
            .createFromResources("menu/shop/categories/special_items.yml", this)
            .createFromResources("menu/shop/categories/stone.yml", this)
            .createFromResources("menu/shop/categories/wood.yml", this)
            .createFromResources("menu/home/config.yml", this)
            .createFromResources("menu/home/editor/home_editor.yml", this)
            .createFromResources("menu/warp/config.yml", this)
            .createFromResources("menu/warp/owner_warps.yml", this)
            .createFromResources("menu/warp/editor/warp_editor.yml", this)
            .createFromResources("menu/levels/ores.yml", this)
            .create("jails.yml")
        localeConfig.loadLocale("locale/cs_cz.properties")
        DatabaseConnector(this).initConnect(
            config.getString("database.host") ?: "jdbc:sqlite:plugins/MagentaPro/database.db",
            config.getString("database.username") ?: "root",
            config.getString("database.password") ?: "admin"
        )
        DatabaseConnector(this).initGeoMaxMind()
    }

    override fun onEnable() {
        val time = measureTime {
            isPaperServer()
            voteParty.createTable()
            commandManager.registerCommands()
            registerTasks()
            handlerListener()
            hookRegistration()
        }
        logger.info("Plugin enabled in time ${time.inWholeSeconds}")
    }



    override fun onDisable() {
        logger.info("Plugin disabled")
        afk.clear()
    }

    private fun isPaperServer() {
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent")
            paperContainerProvider = PaperContainerProvider()
            logger.info("PaperContainerProvider can work on PaperMC !")
        } catch (e : ClassNotFoundException) {
            logger.severe(e.message ?: e.localizedMessage)
        }
    }

    private fun hookRegistration() {
        hookManger.hookLuckPerms()
        hookManger.hookMythicMobs()
        hookManger.hookVault()
        hookManger.hookCreditLite()
        hookManger.hookPAPI()
        hookManger.hookNuVotifier()
    }

    private fun registerTasks() {
        SchedulerMagenta.runTaskTimerAsync(this, BroadcastNewsTask(this), Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong(), Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong())
        SchedulerMagenta.runTaskTimerAsync(this, JailCountDownTask(this), 20, 20)
        SchedulerMagenta.runTaskTimerAsync(this, LevelUpTask(this), 20, 1)
    }

    private fun handlerListener() {
        val list: ArrayList<Listener> = arrayListOf(
            AsyncChatListener(this),
            AsyncFilterChat(this),
            EntityAttackListener(this),
            BlockListener(this),
            PlayerListener(this),
            PlayerCommandPreprocessListener(this),
            SignListener(this),
            PrivateMessageListener(this),
            PortalListener(this),
            HomeListeners(this),
            JailInfoListener(this),
            JailCheckListener(this),
            JailCreateListener(this),
            JailDeleteListener(this),
            JailListener(this),
            JailPardonListener(this),
            JailTeleportListener(this),
            KitListeners(this),
            SocialSpyListener(this),
            TpaListener(this),
            VirtualPlayerLevelListener(this),
            WarpListeners(this)
        )

        val (value, time) = measureTimedValue {
            if (list.isEmpty()) {
                logger.info("Registering list of listeners is empty !")
            }
            list.forEach { pluginManager.registerEvents(it, this) }
        }

        logger.info("Bukkit listeners registered (${list.size}) in time $time -> $value")
        list.removeAll(list.toSet())
    }
}