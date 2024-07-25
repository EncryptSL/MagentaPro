package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.datetime
import com.github.encryptsl.kmono.lib.extensions.parseMinecraftTime
import com.github.encryptsl.kmono.lib.utils.TextFilReader
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import com.github.encryptsl.magenta.common.extensions.sendConsoleCommand
import com.github.encryptsl.magenta.common.model.VoucherManager
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import kotlin.jvm.optionals.getOrNull


class PlayerListener(private val magenta: Magenta) : Listener {

    private val voucherManager: VoucherManager by lazy { VoucherManager(magenta) }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        magenta.levelAPI.createAccount(LevelEntity(event.name, uuid.toString(), 1, 0))
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (!magenta.config.getString("custom-join-message").equals("none", true)) {
            if (user.isVanished())
                event.joinMessage(null)
            else
                event.joinMessage(ModernText.miniModernText(magenta.config.getString("custom-join-message").toString(),
                    TagResolver.resolver(Placeholder.component("player", player.displayName()))
                ))
        }

        safeFly(player)
        user.forceVanish()

        magenta.earnBlocksProgressManager.syncInitData(player.uniqueId, user.getAccount().getInt("mined.blocks", 0))

        if (player.hasPlayedBefore()) {
            user.set(
                mutableMapOf(
                    "timestamps.login" to System.currentTimeMillis(),
                    "ip-address" to player.address.address.hostAddress
                )
            )
            TextFilReader.getReadableFile(magenta.dataFolder, "motd.txt").forEach { text ->
                player.sendMessage(ModernText.miniModernTextCenter(text, TagResolver.resolver(
                    Placeholder.component("player", player.displayName()),
                    Placeholder.parsed("online", Bukkit.getOnlinePlayers().size.toString()),
                    Placeholder.parsed("worldtime", player.world.time.parseMinecraftTime()),
                    Placeholder.parsed("realtime", datetime())
                )))
            }
            if (user.getAccount().contains("votifier.rewards")) {
                player.sendMessage(magenta.locale.translation("magenta.command.vote.success.exist.rewards.to.claim"))
            }
            return
        }

        magenta.spawnConfig.getConfig().getLocation(magenta.config.getString("spawnpoint").toString())?.let {
            player.teleportAsync(it)
        }

        magenta.kitManager.giveKit(player, magenta.config.getString("newbies.kit").toString())

        Bukkit.broadcast(ModernText.miniModernText(magenta.config.getString("newbies.announcement").toString(), TagResolver.resolver(
            Placeholder.parsed("player", player.name),
            Placeholder.parsed("joined", Bukkit.getOfflinePlayers().size.toString())
        )))
        user.createDefaultData(player)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerJoinCheckJail(event: PlayerJoinEvent) {
        magenta.pluginManager.callEvent(JailCheckEvent(event.player))
    }

    private fun safeFly(player: Player) {
        if (!player.hasPermission(Permissions.FLY_SAFE_LOGIN)) return

        player.fallDistance = 0F
        player.allowFlight = true
        player.isFlying = true
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (!magenta.config.getString("custom-quit-message").equals("none", true)) {
            if (user.isVanished())
                event.quitMessage(null)
            else
                event.quitMessage(ModernText.miniModernText(magenta.config.getString("custom-quit-message").toString(),
                    TagResolver.resolver(Placeholder.component("player", player.displayName())))
                )
        }

        magenta.playerCacheManager.reply.invalidate(player)
        magenta.playerCacheManager.antiSpam.invalidate(player.uniqueId)
        magenta.playerCacheManager.teleportRequest.invalidate(player.uniqueId)
        magenta.earnBlocksProgressManager.save(player.uniqueId)
        magenta.earnBlocksProgressManager.remove(player.uniqueId)
        magenta.afk.clear(player.uniqueId)
        user.saveQuitData(player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        user.saveLastLocation(player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        magenta.afk.setTime(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInventory(event: InventoryClickEvent) {
        val top = event.view.topInventory
        val type = top.type

        val whoClicked = event.whoClicked
        if (type == InventoryType.PLAYER) {
            val ownerInv = top.holder ?: return
            if (ownerInv is HumanEntity) {
                if (!whoClicked.hasPermission(Permissions.INVSEE_MODIFY) && ownerInv.hasPermission(Permissions.INVSEE_PREVENT_MODIFY)) {
                    if (whoClicked.hasPermission(Permissions.INVSEE_PREVENT_MODIFY_EXEMPT)) return
                    event.isCancelled = true
                }
            }
        } else if (type == InventoryType.ENDER_CHEST) {
            if (!whoClicked.hasPermission(Permissions.ECHEST_MODIFY)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInteraction(event: PlayerInteractEvent) {
        val player = event.player
        val inventory = player.inventory
        val itemInHand = inventory.itemInMainHand
        magenta.afk.setTime(player.uniqueId)

        if (!itemInHand.hasItemMeta()) return

        val vouchers = magenta.vouchers.getConfig().getConfigurationSection("vouchers")?.getKeys(false) ?: return
        val voucher = vouchers.stream().filter { el -> voucherManager.isItemVoucherInHand(itemInHand, el) }.findFirst().getOrNull() ?: return

        if (event.action.isRightClick) {
            val command = magenta.vouchers.getConfig().getString(("vouchers." + voucher) + ".command").toString()
            sendConsoleCommand(command, player)
            if (itemInHand.amount > 1) {
                itemInHand.amount -= 1
            } else {
                inventory.remove(itemInHand)
            }
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerInteractSignWarp(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        if (!block.type.name.endsWith("_SIGN")) return

        val sign: Sign = block.state as Sign
        val side = sign.getSide(Side.FRONT)

        if (!side.line(0).toString().contains(magenta.locale.translation("magenta.sign.warp").toString()))
            return

        if (!sign.isWaxed) {
            sign.isWaxed = true
        }

        val convertedWarpName = ModernText.convertComponentToText(side.line(1))

        if (convertedWarpName.isBlank() || convertedWarpName.isEmpty()) {
            block.breakNaturally()
            return player.sendMessage(magenta.locale.translation("magenta.sign.warp.error.name.empty"))
        }

        if (!magenta.warpModel.getWarpExist(convertedWarpName).join()) {
            block.breakNaturally()
            return player.sendMessage(magenta.locale.translation("magenta.sign.warp.error.not.exist", Placeholder.component("warp", side.line(1))))
        }

        player.teleport(magenta.warpModel.toLocation(convertedWarpName))
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val player = event.player
        magenta.homeModel.getHomesByOwner(player.uniqueId).thenApply { homes ->
            val home = homes.firstOrNull()
            home?.let { Bukkit.getWorld(home.world)?.let { player.teleportAsync(Location(it, home.x.toDouble(), home.y.toDouble(), home.z.toDouble(), home.yaw, home.pitch)) } }
        }.exceptionally {
            magenta.spawnConfig.getConfig().getLocation("spawn")?.let { player.teleportAsync(it) }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val player = event.player

        if (!magenta.config.getBoolean("change-world-message")) return

        player.sendMessage(
            magenta.locale.translation("magenta.player.change.world",
                Placeholder.parsed("world", player.world.name))
        )
    }
}