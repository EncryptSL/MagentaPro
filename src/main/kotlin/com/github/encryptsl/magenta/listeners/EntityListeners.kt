package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.economy.EconomyTransactionResponse
import com.github.encryptsl.kmono.lib.api.economy.components.EconomyDeposit
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.halloween.HalloweenAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom

class EntityListeners(private val magenta: Magenta) : HalloweenAPI(), Listener {

    @EventHandler
    fun onEntityAttack(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (entity is Player) {
            val player = entity.player ?: return
            val user = magenta.user.getUser(player.uniqueId)

            if (event.cause  != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return
            if (!user.isJailed() || !user.isAfk()) return

            event.isCancelled = true
        }
        if (entity is Mob) {
            if (event.damager !is Player) return
            if (event.cause  != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return

            val player = event.damager as Player
            val user = magenta.user.getUser(player.uniqueId)

            if (!user.isJailed() || !user.isAfk()) return

            player.sendMessage(magenta.locale.translation("magenta.command.jail.error.event", Placeholder.parsed("action", "útočit")))
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerAttackEntity(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && isHalloweenDay()) {
            val player = event.damager as Player

            if (!magenta.config.contains("jobs.hunter") || !magenta.config.contains("jobs.hunter.earn_money")) return

            val earnMoney = magenta.config.getDouble("halloween.reward_multiplier").times(magenta.config.getDouble("jobs.hunter.earn_money", 5.0))
            val transaction = EconomyDeposit(player, earnMoney).transaction(magenta.vaultHook) ?: return

            if (transaction == EconomyTransactionResponse.SUCCESS) {
                player.sendActionBar(ModernText.miniModernText(magenta.config.getString("jobs.hunter.earn_bar").toString(),
                    Placeholder.parsed("value", earnMoney.toString())
                ))
            }
        }
    }

    @EventHandler
    fun onEntityCreatureSpawnHalloween(event: CreatureSpawnEvent) {
        val random = ThreadLocalRandom.current()
        if (isHalloweenSeason() || isHalloweenDay()) {
            val equipment = event.entity.equipment ?: return
            if (event.entityType == EntityType.ZOMBIE) {
                if (equipment.getItem(EquipmentSlot.HEAD).isEmpty == true) {
                    if (random.nextFloat() < 0.25F) {
                        val helmet = if (random.nextFloat() < 0.1F) Material.JACK_O_LANTERN else Material.CARVED_PUMPKIN
                        equipment.setItem(EquipmentSlot.HEAD, ItemStack(helmet, 1))
                    }
                }
            }

            if (event.entityType == EntityType.SKELETON) {
                if (equipment.getItem(EquipmentSlot.HEAD).isEmpty == true) {
                    if (random.nextFloat() < 0.25F) {
                        val helmet = if (random.nextFloat() < 0.1F) Material.JACK_O_LANTERN else Material.CARVED_PUMPKIN
                        equipment.setItem(EquipmentSlot.HEAD, ItemStack(helmet, 1))
                    }
                }
            }
        }
    }
}