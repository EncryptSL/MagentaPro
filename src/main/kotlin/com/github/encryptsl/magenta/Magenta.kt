package com.github.encryptsl.magenta

import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.kmono.lib.api.config.loader.ConfigLoader
import com.github.encryptsl.kmono.lib.api.config.locale.Locale
import com.github.encryptsl.kmono.lib.api.discord.DiscordWebhook
import com.github.encryptsl.magenta.api.ItemFactory
import com.github.encryptsl.magenta.api.account.User
import com.github.encryptsl.magenta.api.containers.PaperContainerProvider
import com.github.encryptsl.magenta.api.level.VirtualLevelAPI
import com.github.encryptsl.magenta.api.votes.MagentaVoteAPI
import com.github.encryptsl.magenta.api.votes.MagentaVotePartyAPI
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.CommandManager
import com.github.encryptsl.magenta.common.PlayerCacheManager
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.database.GeoMaxMindDatabase
import com.github.encryptsl.magenta.common.database.models.HomeModel
import com.github.encryptsl.magenta.common.database.models.LevelModel
import com.github.encryptsl.magenta.common.database.models.VotePartyModel
import com.github.encryptsl.magenta.common.database.models.WarpModel
import com.github.encryptsl.magenta.common.filter.ChatChecksManager
import com.github.encryptsl.magenta.common.hook.HookManager
import com.github.encryptsl.magenta.common.hook.plugins.vaultunlocked.VaultUnlockedHook
import com.github.encryptsl.magenta.common.model.*
import com.github.encryptsl.magenta.common.tasks.BroadcastNewsTask
import com.github.encryptsl.magenta.common.tasks.JailCountDownTask
import com.github.encryptsl.magenta.common.tasks.LevelUpTask
import com.github.encryptsl.magenta.common.utils.AfkUtils
import com.github.encryptsl.magenta.common.utils.StringUtils
import com.github.encryptsl.magenta.listeners.*
import com.github.encryptsl.magenta.listeners.custom.*
import com.tcoded.folialib.FoliaLib
import io.papermc.paper.util.Tick
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import kotlin.time.measureTime

open class Magenta : JavaPlugin() {

    lateinit var paperContainerProvider: PaperContainerProvider

    companion object {
        lateinit var scheduler: FoliaLib
        lateinit var instance: Magenta
            private set
    }

    val path = dataFolder
    var random = ThreadLocalRandom.current().nextInt(1000, 9999)
    val pluginManager = server.pluginManager
    val database: DatabaseConnector by lazy { DatabaseConnector(this) }
    val user: User by lazy { User() }
    val vote: MagentaVoteAPI by lazy { MagentaVoteAPI() }
    val voteParty: MagentaVotePartyAPI by lazy { MagentaVotePartyAPI(VotePartyModel()) }
    val virtualLevel: VirtualLevelAPI by lazy { VirtualLevelAPI(this) }
    val stringUtils: StringUtils by lazy { StringUtils(this) }
    val homeModel: HomeModel by lazy { HomeModel(this) }
    val warpModel: WarpModel by lazy { WarpModel(this) }
    val levelModel: LevelModel by lazy { LevelModel() }
    val kitManager: KitManager by lazy { KitManager(this) }
    val tpaManager: TpaManager by lazy { TpaManager(this) }
    val afk: AfkUtils by lazy { AfkUtils(this) }
    val itemFactory: ItemFactory by lazy { ItemFactory() }

    val locale: Locale by lazy { Locale(this) }
    val kitConfig: UniversalConfig by lazy { UniversalConfig("${path}/kits.yml") }
    val jailConfig: UniversalConfig by lazy { UniversalConfig("${path}/jails.yml") }
    val mmConfig: UniversalConfig by lazy { UniversalConfig("${path}/mythicmobs/rewards.yml") }
    val vouchers: UniversalConfig by lazy { UniversalConfig("${path}/vouchers.yml") }
    val randomConfig: UniversalConfig by lazy { UniversalConfig("${path}/random.yml") }
    val shopConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/shop/shop.yml") }
    val creditShopConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/creditshop/shop.yml") }
    val creditShopConfirmMenuConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/creditshop/confirm_menu.yml") }
    val milestonesOres: UniversalConfig by lazy { UniversalConfig("${path}/menu/milestones/ores.yml") }
    val milestonesVotePass: UniversalConfig by lazy { UniversalConfig("${path}/menu/milestones/vote_pass.yml") }
    val homeMenuConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/home/config.yml") }
    val homeEditorConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/home/editor/home_editor.yml") }
    val warpMenuConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/warp/config.yml") }
    val warpPlayerMenuConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/warp/owner_warps.yml") }
    val warpEditorConfig: UniversalConfig by lazy { UniversalConfig("${path}/menu/warp/editor/warp_editor.yml") }
    val chatControl: UniversalConfig by lazy { UniversalConfig("${path}/chatcontrol/filter.yml") }
    val oraxenControl: UniversalConfig by lazy { UniversalConfig("${path}/oraxen/config.yml") }
    val serverFeedback: DiscordWebhook by lazy { DiscordWebhook(config.getString("discord.webhooks.server_feedback").toString()) }
    val notification: DiscordWebhook by lazy { DiscordWebhook(config.getString("discord.webhooks.notifications").toString()) }
    val jailManager: JailManager by lazy { JailManager(this) }

    val newsQueueManager: NewsQueueManager by lazy { NewsQueueManager(this) }
    val earnBlocksProgressManager: EarnBlocksProgressManager by lazy { EarnBlocksProgressManager(this) }
    val commandHelper: CommandHelper by lazy { CommandHelper(this) }

    val playerCacheManager by lazy { PlayerCacheManager(this) }
    val vaultUnlockedHook by lazy { VaultUnlockedHook(this) }

    val commandManager: CommandManager by lazy { CommandManager(this) }
    val geoMaxMind: GeoMaxMindDatabase by lazy { GeoMaxMindDatabase(this) }
    private val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    private val hookManger: HookManager by lazy { HookManager(this) }
    private val chatChecksManager: ChatChecksManager by lazy { ChatChecksManager(this) }

    override fun onLoad() {
        instance = this
        configLoader
            .createFromResources("locale/cs_cz.properties")
            .createFromResources("kits.yml")
            .createFromResources("config.yml")
            .createFromResources("chatcontrol/swears.txt")
            .createFromResources("chatcontrol/filter.yml")
            .createFromResources("motd.txt")
            .createFromResources("vouchers.yml")
            .createFromResources("random.yml")
            .createFromResources("oraxen/config.yml")
            .createFromResources("mythicmobs/config.yml")
            .createFromResources("menu/shop/shop.yml")
            .createFromResources("menu/creditshop/shop.yml")
            .createFromResources("menu/creditshop/confirm_menu.yml")
            .createFromResources("menu/creditshop/categories/galaxy_box_keys.yml")
            .createFromResources("menu/shop/categories/blocks.yml")
            .createFromResources("menu/shop/categories/decoration.yml")
            .createFromResources("menu/shop/categories/farms.yml")
            .createFromResources("menu/shop/categories/food.yml")
            .createFromResources("menu/shop/categories/materials.yml")
            .createFromResources("menu/shop/categories/ores.yml")
            .createFromResources("menu/shop/categories/redstones.yml")
            .createFromResources("menu/shop/categories/special_items.yml")
            .createFromResources("menu/shop/categories/stone.yml")
            .createFromResources("menu/shop/categories/wood.yml")
            .createFromResources("menu/home/config.yml")
            .createFromResources("menu/home/editor/home_editor.yml")
            .createFromResources("menu/warp/config.yml")
            .createFromResources("menu/warp/owner_warps.yml")
            .createFromResources("menu/warp/editor/warp_editor.yml")
            .createFromResources("menu/milestones/ores.yml")
            .createFromResources("menu/milestones/vote_pass.yml")
            .create("jails.yml")
        locale.loadLocale("locale/cs_cz.properties")
        database.createConnection(
            config.getString("database.host", "jdbc:sqlite:plugins/MagentaPro/database.db").toString(),
            config.getString("database.username", "root").toString(),
            config.getString("database.password", "admin").toString()
        )
        geoMaxMind.initGeoMaxMind(config.getString("maxmind-url", "https://git.io/GeoLite2-Country.mmdb").toString())
        voteParty.createTable()
    }

    override fun onEnable() {
        scheduler = FoliaLib(this)
        val time = measureTime {
            isPaperServer()
            commandManager.registerCommands()
            newsQueueManager.loadQueue()
            registerTasks()
            chatChecksManager.initializeChecks()
            handlerListener()
            hookManger.hookPlugins()
        }
        logger.info("Plugin enabled in time ${time.inWholeSeconds}")
    }



    override fun onDisable() {
        logger.info("Plugin disabled")
        earnBlocksProgressManager.saveMinedBlocks()
        afk.clear()
        playerCacheManager.reply.invalidateAll()
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

    private fun registerTasks() {
        scheduler.impl.runTimer(
            BroadcastNewsTask(this),
            Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong(),
            Tick.tick().fromDuration(Duration.ofSeconds(config.getLong("news.period", 10))).toLong()
        )
        scheduler.impl.runTimer(JailCountDownTask(this), 20, 20)
        scheduler.impl.runTimer(LevelUpTask(this), 20, 1)
    }

    private fun handlerListener() {
        val list: ArrayList<Listener> = arrayListOf(
            AsyncChatListener(this),
            EntityListeners(this),
            BlockListener(this),
            PlayerListener(this),
            CommandListener(this),
            SignListener(this),
            PrivateMessageListener(this),
            PortalListener(this),
            HomeListeners(this),
            JailListeners(this),
            KitListeners(this),
            SocialSpyListener(this),
            TpaListener(this),
            VirtualPlayerLevelListener(this),
            WarpListeners(this)
        )

        val time = measureTime {
            val iterator = list.iterator()
            while (iterator.hasNext()) { pluginManager.registerEvents(iterator.next(), this) }
        }

        logger.info("Bukkit listeners registered (${list.size}) in time ${time.inWholeSeconds}")
        list.removeAll(list.toSet())
    }
}