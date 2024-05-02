package com.github.encryptsl.magenta

import com.github.encryptsl.magenta.api.ItemFactory
import com.github.encryptsl.magenta.api.account.User
import com.github.encryptsl.magenta.api.config.UniversalConfig
import com.github.encryptsl.magenta.api.config.loader.ConfigLoader
import com.github.encryptsl.magenta.api.config.locale.Locale
import com.github.encryptsl.magenta.api.containers.PaperContainerProvider
import com.github.encryptsl.magenta.api.level.VirtualLevelAPI
import com.github.encryptsl.magenta.api.votes.MagentaVoteAPI
import com.github.encryptsl.magenta.api.votes.MagentaVotePartyAPI
import com.github.encryptsl.magenta.api.webhook.DiscordWebhook
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.CommandManager
import com.github.encryptsl.magenta.common.PlayerCacheManager
import com.github.encryptsl.magenta.common.database.DatabaseConnector
import com.github.encryptsl.magenta.common.database.models.HomeModel
import com.github.encryptsl.magenta.common.database.models.LevelModel
import com.github.encryptsl.magenta.common.database.models.VotePartyModel
import com.github.encryptsl.magenta.common.database.models.WarpModel
import com.github.encryptsl.magenta.common.hook.HookManager
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.model.EarnBlocksProgressManager
import com.github.encryptsl.magenta.common.model.JailManager
import com.github.encryptsl.magenta.common.model.KitManager
import com.github.encryptsl.magenta.common.model.NewsQueueManager
import com.github.encryptsl.magenta.common.model.TpaManager
import com.github.encryptsl.magenta.common.tasks.BroadcastNewsTask
import com.github.encryptsl.magenta.common.tasks.JailCountDownTask
import com.github.encryptsl.magenta.common.tasks.LevelUpTask
import com.github.encryptsl.magenta.common.utils.AfkUtils
import com.github.encryptsl.magenta.common.utils.StringUtils
import com.github.encryptsl.magenta.listeners.*
import com.github.encryptsl.magenta.listeners.CommandListener
import com.github.encryptsl.magenta.listeners.custom.*
import fr.euphyllia.energie.Energie
import fr.euphyllia.energie.model.Scheduler
import fr.euphyllia.energie.model.SchedulerType
import io.papermc.paper.util.Tick
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import kotlin.time.measureTime

open class Magenta : JavaPlugin() {

    lateinit var paperContainerProvider: PaperContainerProvider

    companion object {
        lateinit var scheduler: Scheduler
        lateinit var instance: Magenta
            private set
    }

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
    val kitConfig: UniversalConfig by lazy { UniversalConfig("kits.yml") }
    val jailConfig: UniversalConfig by lazy { UniversalConfig("jails.yml") }
    val mmConfig: UniversalConfig by lazy { UniversalConfig("mythicmobs/rewards.yml") }
    val cItems: UniversalConfig by lazy { UniversalConfig("citems.yml") }
    val randomConfig: UniversalConfig by lazy { UniversalConfig("random.yml") }
    val shopConfig: UniversalConfig by lazy { UniversalConfig("menu/shop/shop.yml") }
    val creditShopConfig: UniversalConfig by lazy { UniversalConfig("menu/creditshop/shop.yml") }
    val creditShopConfirmMenuConfig: UniversalConfig by lazy { UniversalConfig("menu/creditshop/confirm_menu.yml") }
    val milestonesOres: UniversalConfig by lazy { UniversalConfig("menu/milestones/ores.yml") }
    val milestonesVotePass: UniversalConfig by lazy { UniversalConfig("menu/milestones/vote_pass.yml") }
    val homeMenuConfig: UniversalConfig by lazy { UniversalConfig("menu/home/config.yml") }
    val homeEditorConfig: UniversalConfig by lazy { UniversalConfig("menu/home/editor/home_editor.yml") }
    val warpMenuConfig: UniversalConfig by lazy { UniversalConfig("menu/warp/config.yml") }
    val warpPlayerMenuConfig: UniversalConfig by lazy { UniversalConfig("menu/warp/owner_warps.yml") }
    val warpEditorConfig: UniversalConfig by lazy { UniversalConfig("menu/warp/editor/warp_editor.yml") }
    val chatControl: UniversalConfig by lazy { UniversalConfig("chatcontrol/filter.yml") }
    val oraxenControl: UniversalConfig by lazy { UniversalConfig("oraxen/config.yml") }
    val serverFeedback: DiscordWebhook by lazy { DiscordWebhook(config.getString("discord.webhooks.server_feedback").toString()) }
    val notification: DiscordWebhook by lazy { DiscordWebhook(config.getString("discord.webhooks.notifications").toString()) }
    val jailManager: JailManager by lazy { JailManager(jailConfig.getConfig()) }

    val newsQueueManager: NewsQueueManager by lazy { NewsQueueManager(this) }
    val earnBlocksProgressManager: EarnBlocksProgressManager by lazy { EarnBlocksProgressManager(this) }
    val commandHelper: CommandHelper by lazy { CommandHelper(this) }

    val playerCacheManager by lazy { PlayerCacheManager() }
    val vaultHook by lazy { VaultHook(this) }

    val commandManager: CommandManager by lazy { CommandManager(this) }
    private val configLoader: ConfigLoader by lazy { ConfigLoader(this) }
    private val hookManger: HookManager by lazy { HookManager(this) }
    private val energie: Energie by lazy {Energie(this)}

    override fun onLoad() {
        instance = this
        configLoader
            .createFromResources("locale/cs_cz.properties", this)
            .createFromResources("kits.yml", this)
            .createFromResources("config.yml", this)
            .createFromResources("chatcontrol/swears.txt", this)
            .createFromResources("chatcontrol/filter.yml", this)
            .createFromResources("motd.txt", this)
            .createFromResources("citems.yml", this)
            .createFromResources("random.yml", this)
            .createFromResources("oraxen/config.yml", this)
            .createFromResources("mythicmobs/config.yml", this)
            .createFromResources("menu/shop/shop.yml", this)
            .createFromResources("menu/creditshop/shop.yml", this)
            .createFromResources("menu/creditshop/confirm_menu.yml", this)
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
            .createFromResources("menu/milestones/ores.yml", this)
            .createFromResources("menu/milestones/vote_pass.yml", this)
            .create("jails.yml")
        locale.loadLocale("locale/cs_cz.properties")
        database.initConnect(
            config.getString("database.host", "jdbc:sqlite:plugins/MagentaPro/database.db").toString(),
            config.getString("database.username", "root").toString(),
            config.getString("database.password", "admin").toString()
        )
        database.initGeoMaxMind(config.getString("maxmind-url").toString())
    }

    override fun onEnable() {
        val time = measureTime {
            isPaperServer()
            scheduler = energie.minecraftScheduler
            voteParty.createTable()
            commandManager.registerCommands()
            newsQueueManager.loadQueue()
            hookManger.hookPlugins()
            registerTasks()
            handlerListener()
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
        scheduler.runAtFixedRate(SchedulerType.SYNC, BroadcastNewsTask(this), Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong(), Tick.tick().fromDuration(Duration.ofMinutes(config.getLong("news.delay"))).toLong())
        scheduler.runAtFixedRate(SchedulerType.SYNC, JailCountDownTask(this), 20, 20)
        scheduler.runAtFixedRate(SchedulerType.SYNC, LevelUpTask(this), 20, 1)
    }

    private fun handlerListener() {
        val list: ArrayList<Listener> = arrayListOf(
            AsyncChatListener(this),
            AsyncFilterChat(this),
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
            if (list.isEmpty()) {
                logger.info("Registering list of listeners is empty !")
            }
            for (e in list) {pluginManager.registerEvents(e, this)}
        }

        logger.info("Bukkit listeners registered (${list.size}) in time ${time.inWholeSeconds}")
        list.removeAll(list.toSet())
    }
}