package com.github.encryptsl.magenta.listeners

import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class EntityAttackListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onEntityAttack(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (entity is Player) {
            val player = entity.player ?: return
            if (event.cause  != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return
            if (!magenta.user.getUser(player.uniqueId).isJailed()) return

            event.isCancelled = true
        }
        if (entity is Mob) {
            if (event.damager !is Player) return
            if (event.cause  != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return

            val player = event.damager as Player

            if (!magenta.user.getUser(player.uniqueId).isJailed()) return

            player.sendMessage(magenta.localeConfig.translation("magenta.command.jail.error.event", Placeholder.parsed("action", "útočit")))
            event.isCancelled = true
        }
    }


}