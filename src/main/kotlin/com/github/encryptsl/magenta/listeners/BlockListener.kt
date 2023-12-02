package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import com.github.encryptsl.magenta.common.utils.BlockUtils
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import java.util.UUID

class BlockListener(private val magenta: Magenta) : Listener {

    private val earnMoneyProgress = HashMap<UUID, Int>()

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val silkyTools = magenta.config.getStringList("silky.tools")

        val jailEvent = JailPlayerEvent(player, "ničit bloky")
        magenta.pluginManager.callEvent(jailEvent)
        if (jailEvent.isCancelled) {
            event.isCancelled = true
        }

        if (magenta.config.getBoolean("silky.enabled")) {
            if (block.type == Material.SPAWNER) {
                val itemInHand = player.inventory.itemInMainHand
                val tools = silkyTools.contains(itemInHand.type.name)
                if (tools && itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    if (!player.hasPermission("magenta.silky.spawner"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.silky.spawner.error.permissions")))

                    BlockUtils.dropSpawner(block)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakEarnMoney(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val material = block.type
        if (!magenta.config.getStringList("jobs.whitelist_world").contains(block.world.name)) return
        if (!magenta.config.getStringList("jobs.miner.blocks").contains(material.name)) return

        earnMoneyProgress.putIfAbsent(player.uniqueId, 0)
        earnMoneyProgress.computeIfPresent(player.uniqueId) { _, progress -> progress + 1}
        val currentProgress = earnMoneyProgress[player.uniqueId] ?: 0

        player.sendActionBar(ModernText.miniModernText(magenta.config.getString("jobs.miner.action_bar").toString(), TagResolver.resolver(
            Placeholder.parsed("current_progress", currentProgress.toString()),
            Placeholder.parsed("max_progress", magenta.config.getInt("jobs.miner.max_progress").toString()),
        )))

        if (currentProgress >= magenta.config.getInt("jobs.miner.max_progress")) {
            player.sendActionBar(ModernText.miniModernText(magenta.config.getString("jobs.miner.earn_bar").toString(), TagResolver.resolver(
                Placeholder.parsed("current_progress", currentProgress.toString()),
                Placeholder.parsed("max_progress", magenta.config.getInt("jobs.miner.max_progress").toString()),
            )))
            earnMoneyProgress.remove(player.uniqueId)
            magenta.config.getStringList("jobs.miner.rewards").forEach {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), magenta.stringUtils.magentaPlaceholders(it, player))
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreakLevelOre(event: BlockBreakEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val block = event.block

        if (player.hasPermission("magenta.level.mining.bypass")) return
        val (_, _, level, _) = magenta.levelModel.getLevel(uuid)
        if (!magenta.config.contains("level.ores.${block.type.name}")) return

        if (!magenta.config.getStringList("level.worlds").contains(player.world.name)) return

        val requiredLevel = magenta.config.getInt("level.ores.${block.type.name}")
        if (requiredLevel > level) {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.mining.level.required"), TagResolver.resolver(
                Placeholder.parsed("level", level.toString()),
                Placeholder.parsed("required_level", requiredLevel.toString())
            )))
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        val jailEvent = JailPlayerEvent(player, "pokládat bloky")
        magenta.pluginManager.callEvent(jailEvent)
        if (jailEvent.isCancelled) {
            event.isCancelled = true
        }

        BlockUtils.updateSpawner(event.block, player)
    }

}