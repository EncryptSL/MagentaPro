package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailCheckEvent
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import com.github.encryptsl.magenta.common.extensions.datetime
import com.github.encryptsl.magenta.common.extensions.parseMinecraftTime
import com.github.encryptsl.magenta.common.utils.FileUtil
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*

class PlayerListener(private val magenta: Magenta) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        val player = event.uniqueId
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        magenta.virtualLevel.createAccount(LevelEntity(event.name, player.toString(), 1, 0))
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (!magenta.config.getString("custom-join-message").equals("none", ignoreCase = true)) {
            if (user.isVanished())
                event.joinMessage(null)
            else
                event.joinMessage(ModernText.miniModernText(magenta.config.getString("custom-join-message").toString(),
                    TagResolver.resolver(Placeholder.component("player", player.displayName()))
                ))
        }

        safeFly(player)
        magenta.commandHelper.doVanish(player, user.isVanished())

        magenta.earnBlocksProgress.putIfAbsent(player.uniqueId, user.getAccount().getInt("mined.blocks", 0))

        if (player.hasPlayedBefore()) {
            user.set("timestamps.login", System.currentTimeMillis())

            FileUtil.getReadableFile(magenta.dataFolder, "motd.txt").forEach { text ->
                player.sendMessage(ModernText.miniModernTextCenter(text, TagResolver.resolver(
                    Placeholder.component("player", player.displayName()),
                    Placeholder.parsed("online", Bukkit.getOnlinePlayers().size.toString()),
                    Placeholder.parsed("worldtime", player.world.time.parseMinecraftTime()),
                    Placeholder.parsed("realtime", datetime())
                )))
            }
            if (user.getAccount().contains("votifier.rewards")) {
                player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.exist.rewards.to.claim")))
            }
            return
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
        if (player.hasPermission("magenta.fly.safelogin")) {
            player.fallDistance = 0F
            player.allowFlight = true
            player.isFlying = true
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)

        if (!magenta.config.getString("custom-quit-message").equals("none", ignoreCase = true)) {
            if (user.isVanished())
                event.quitMessage(null)
            else
                event.quitMessage(ModernText.miniModernText(magenta.config.getString("custom-quit-message").toString(),
                    TagResolver.resolver(Placeholder.component("player", player.displayName())))
                )
        }

        user.set("mined.blocks", magenta.earnBlocksProgress[player.uniqueId] ?: 0)
        magenta.afk.clear(player.uniqueId)
        user.saveQuitData(player)
        user.saveLastLocation(player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val user = magenta.user.getUser(player.uniqueId)
        if (user.isJailed()) return
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
                if (!whoClicked.hasPermission("magenta.invsee.modify") && ownerInv.hasPermission("magenta.invsee.prevent.modify")) {
                    if (whoClicked.hasPermission("magenta.invsee.prevent.modify.exempt")) return
                    event.isCancelled = true
                }
            }
        } else if (type == InventoryType.ENDER_CHEST) {
            if (!whoClicked.hasPermission("magenta.echest.modify")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInteraction(event: PlayerInteractEvent) {
        val player = event.player
        magenta.afk.setTime(player.uniqueId)

        if (player.inventory.itemInMainHand.hasItemMeta()) {
            if (event.action.isRightClick) {
                val itemInHand = player.inventory.itemInMainHand
                val itemMeta = itemInHand.itemMeta
                magenta.cItems.getConfig().getConfigurationSection("citems")?.getKeys(false)?.forEach {
                    if (itemMeta.hasDisplayName()) {
                        val sid = magenta.cItems.getConfig().getString("citems.$it.sid").toString()
                        val item = magenta.cItems.getConfig().getString("citems.$it.name").toString().replace("<sid>", sid)
                        if (itemMeta.displayName() == ModernText.miniModernText(item)) {
                            val command = magenta.cItems.getConfig().getString("citems.$it.command").toString()
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), magenta.stringUtils.magentaPlaceholders(command, player))
                            if (itemInHand.amount > 1) {
                                itemInHand.amount -= 1
                            } else {
                                player.inventory.remove(itemInHand)
                            }
                        }
                    }
                }
            }
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

        if (!side.line(0).toString().contains(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.sign.warp")).toString()))
            return

        if (!sign.isWaxed) {
            sign.isWaxed = true
        }

        val convertedWarpName = ModernText.convertComponentToText(side.line(1))

        if (convertedWarpName.isBlank() || convertedWarpName.isEmpty()) {
            block.breakNaturally()
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.sign.warp.error.name.empty")))
        }

        if (!magenta.warpModel.getWarpExist(convertedWarpName)) {
            block.breakNaturally()
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.sign.warp.error.not.exist"), Placeholder.component("warp", side.line(1))))
        }

        player.teleport(magenta.warpModel.toLocation(convertedWarpName))
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val player = event.player

        if (!magenta.config.getBoolean("change-world-message")) return

        player.sendMessage(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.player.change.world"), TagResolver.resolver(
            Placeholder.parsed("world", player.world.name)
        )))
    }
}