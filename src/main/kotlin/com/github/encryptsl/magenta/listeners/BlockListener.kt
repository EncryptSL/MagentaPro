package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.halloween.HalloweenAPI
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.utils.BlockUtils
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockListener(private val magenta: Magenta) : HalloweenAPI(), Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakJail(event: BlockBreakEvent) {
        val player = event.player
        if (!magenta.user.getUser(player.uniqueId).isJailed()) return

        player.sendMessage(magenta.locale.translation("magenta.command.jail.error.event", Placeholder.parsed("action", "ničit bloky")))
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!magenta.config.getBoolean("silky_spawners.enabled") || block.type != Material.SPAWNER) return

        val itemInHand = player.inventory.itemInMainHand
        val tools = magenta.stringUtils.inInList("silky_spawners.tools", itemInHand.type.name)
        val hasToolEnchantment = itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)
        if (!tools && !hasToolEnchantment) return

        if (!player.hasPermission(Permissions.SILKY_SPAWNER))
            return player.sendMessage(magenta.locale.translation("magenta.silky.spawner.error.permissions"))

        BlockUtils.dropSpawner(block, magenta.config.getString("silky_spawners.spawner_name").toString())
    }

    @EventHandler
    fun onBreakWarpSign(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!block.type.name.endsWith("_SIGN")) return

        val sign: Sign = block.state as Sign

        val hasPlayerPermission = player.hasPermission(Permissions.WARP_SIGN_BREAK)

        val isLineSame = sign.getSide(Side.FRONT)
            .line(0)
            .contains(magenta.locale.translation("magenta.sign.warp"))

        if (isLineSame && !hasPlayerPermission) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakLevelOre(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (player.gameMode == GameMode.CREATIVE) return
        if (player.hasPermission(Permissions.LEVEL_MINE_BYPASS)) return
        if (!magenta.config.contains("level.ores.${block.type.name}")) return

        if (!magenta.stringUtils.inInList("level.worlds", player.world.name)) return

        val requiredLevel = magenta.config.getInt("level.ores.${block.type.name}")
        magenta.levelAPI.getUserByUUID(player.uniqueId).thenAccept {
            if (requiredLevel < it.level) return@thenAccept

            player.sendMessage(magenta.locale.translation("magenta.mining.level.required", TagResolver.resolver(
                Placeholder.parsed("block", block.type.name),
                Placeholder.parsed("level", it.level.toString()),
                Placeholder.parsed("required_level", requiredLevel.toString()))
            ))
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (magenta.user.getUser(player.uniqueId).isJailed()) {
            player.sendMessage(magenta.locale.translation("magenta.command.jail.error.event", Placeholder.parsed("action", "pokládat bloky")))
            event.isCancelled = true
        }

        BlockUtils.updateSpawner(event.block, player)
    }

}