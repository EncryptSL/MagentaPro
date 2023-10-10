package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.jail.JailPlayerEvent
import com.github.encryptsl.magenta.common.utils.BlockUtils
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockListener(private val magenta: Magenta) : Listener {

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