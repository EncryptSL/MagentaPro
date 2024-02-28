package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.BlockUtils
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
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

class BlockListener(private val magenta: Magenta) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakJail(event: BlockBreakEvent) {
        val player = event.player
        if (magenta.user.getUser(player.uniqueId).isJailed()) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.event"), TagResolver.resolver(
                Placeholder.parsed("action", "ničit bloky")
            )))
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val silkyTools = magenta.config.getStringList("silky.tools")

        if (!magenta.config.getBoolean("silky.enabled")) return

        if (block.type == Material.SPAWNER) {
            val itemInHand = player.inventory.itemInMainHand
            val tools = silkyTools.contains(itemInHand.type.name)
            if (!tools && !itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) return

            if (!player.hasPermission("magenta.silky.spawner"))
                return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.silky.spawner.error.permissions")))

            BlockUtils.dropSpawner(block)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakEarnMoney(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val material = block.type
        if (!magenta.stringUtils.inInList("jobs.whitelist_world", player.world.name)) return
        if (!magenta.stringUtils.inInList("jobs.miner.blocks", material.name)) return

        val currentProgress = magenta.earnBlocksProgress.computeIfPresent(player.uniqueId) {_, v -> v + 1} ?: 0

        player.sendActionBar(ModernText.miniModernText(magenta.config.getString("jobs.miner.action_bar").toString(), TagResolver.resolver(
            Placeholder.parsed("current_progress", currentProgress.toString()),
            Placeholder.parsed("max_progress", magenta.config.getInt("jobs.miner.max_progress").toString()),
        )))

        if (currentProgress >= magenta.config.getInt("jobs.miner.max_progress")) {
            player.sendActionBar(ModernText.miniModernText(magenta.config.getString("jobs.miner.earn_bar").toString(), TagResolver.resolver(
                Placeholder.parsed("current_progress", currentProgress.toString()),
                Placeholder.parsed("max_progress", magenta.config.getInt("jobs.miner.max_progress").toString()),
            )))
            magenta.earnBlocksProgress[player.uniqueId] = 0
            magenta.config.getStringList("jobs.miner.rewards").forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), magenta.stringUtils.magentaPlaceholders(it, player))
            }
        }
    }

    @EventHandler
    fun onBreakWarpSign(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (block.type.name.endsWith("_SIGN")) {
            val sign: Sign = block.state as Sign
            if (sign.getSide(Side.FRONT).line(0).contains(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.sign.warp"))) && !player.hasPermission("magenta.sign.warp.break")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakLevelOre(event: BlockBreakEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val block = event.block

        if (player.gameMode == GameMode.CREATIVE) return
        if (player.hasPermission("magenta.level.mining.bypass")) return
        val (_, _, level, _) = magenta.levelModel.getLevel(uuid)
        if (!magenta.config.contains("level.ores.${block.type.name}")) return

        if (!magenta.stringUtils.inInList("level.worlds", player.world.name)) return

        val requiredLevel = magenta.config.getInt("level.ores.${block.type.name}")
        if (requiredLevel < level) return

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.mining.level.required"), TagResolver.resolver(
                Placeholder.parsed("block", block.type.name),
                Placeholder.parsed("level", level.toString()),
                Placeholder.parsed("required_level", requiredLevel.toString()))))
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (magenta.user.getUser(player.uniqueId).isJailed()) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.jail.error.event"), TagResolver.resolver(
                Placeholder.parsed("action", "pokládat bloky")
            )))
            event.isCancelled = true
        }

        BlockUtils.updateSpawner(event.block, player)
    }

}