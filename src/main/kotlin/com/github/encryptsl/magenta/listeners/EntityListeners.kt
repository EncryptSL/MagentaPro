package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.kmono.lib.extensions.playSound
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
    fun onEntityFallDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player: Player = event.entity as Player
            if (!magenta.config.getBoolean("void_spawn.enable") || !magenta.stringUtils.inInList("void_spawn.worlds", player.world.name)) return

            if (event.cause == EntityDamageEvent.DamageCause.VOID) {
                magenta.spawnConfig.getConfig().getLocation("spawn")?.let {
                    Magenta.scheduler.impl.teleportAsync(player, it).thenAccept {
                        playSound(player, magenta.config.getString("void_spawn.sound").toString(), 5f, 1f)
                    }
                }
                event.isCancelled = true
            }
        }
    }


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
    fun onEntityCreatureSpawnHalloween(event: CreatureSpawnEvent) {
        val random = ThreadLocalRandom.current()
        if (isHalloweenSeason() || isHalloweenDay()) {
            val equipment = event.entity.equipment ?: return
            if (event.entityType == EntityType.ZOMBIE) {
                if (equipment.getItem(EquipmentSlot.HEAD).isEmpty) {
                    if (random.nextFloat() < 0.25F) {
                        val helmet = if (random.nextFloat() < 0.1F) Material.JACK_O_LANTERN else Material.CARVED_PUMPKIN
                        equipment.setItem(EquipmentSlot.HEAD, ItemStack(helmet, 1))
                    }
                }
            }

            if (event.entityType == EntityType.SKELETON) {
                if (equipment.getItem(EquipmentSlot.HEAD).isEmpty) {
                    if (random.nextFloat() < 0.25F) {
                        val helmet = if (random.nextFloat() < 0.1F) Material.JACK_O_LANTERN else Material.CARVED_PUMPKIN
                        equipment.setItem(EquipmentSlot.HEAD, ItemStack(helmet, 1))
                    }
                }
            }
        }
    }
}