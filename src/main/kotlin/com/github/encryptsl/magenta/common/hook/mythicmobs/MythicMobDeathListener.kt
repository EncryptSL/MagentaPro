package com.github.encryptsl.magenta.common.hook.mythicmobs

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.extensions.forEachIndexed
import com.github.encryptsl.magenta.common.utils.ModernText
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.math.RoundingMode
import java.util.UUID

class MythicMobDeathListener(private val magenta: Magenta) : Listener {

    private val damageMap = HashMap<UUID, Double>()

    @EventHandler
    fun onPlayerKillMythicMob(event: EntityDamageByEntityEvent) {
        if (MythicBukkit.inst().apiHelper.isMythicMob(event.entity)) {
            if (event.damager is Player) {
                damageMap[event.damager.uniqueId] =
                    damageMap.getOrDefault(event.damager.uniqueId, 0.0).plus(event.damage)
            } else if (event.damager is Projectile) {
                val projectile: Projectile = event.damager as Projectile
                if (projectile.shooter is Player) {
                    damageMap[event.damager.uniqueId] =
                        damageMap.getOrDefault(event.damager.uniqueId, 0.0).plus(event.damage)
                }
            }
        }
    }

    @EventHandler
    fun onMythicMobDeath(event: MythicMobDeathEvent) {
        if (event.killer is Player) {
            val entityName = event.mobType.internalName
            if (magenta.mmConfig.getConfig().contains("mm_rewards.$entityName")) {
                val positions = sortedByDamage()
                positions.forEach { (t, u) ->
                    for (message in magenta.mmConfig.getConfig().getStringList("mm_rewards.$entityName.RewardMessage.message")) {
                        Bukkit.getPlayer(t)?.sendMessage(ModernText.miniModernTextCenter(
                                message
                                    .replace("{top_player_1}", nameByRank(1))
                                    .replace("{top_player_2}", nameByRank(2))
                                    .replace("{top_player_3}", nameByRank(3))
                                    .replace("{top_damage_1}", roundingDamage(1).toString())
                                    .replace("{top_damage_2}", roundingDamage(2).toString())
                                    .replace("{top_damage_3}", roundingDamage(3).toString())
                                    .replace("{personal_score}", roundingPersonalDamage(u).toString())
                        ))
                    }
                }

                if (magenta.mmConfig.getConfig().contains("mm_rewards.$entityName.RewardCommands")) {
                    positions.filter { Bukkit.getPlayer(it.key) != null }.entries.forEachIndexed { index, entry ->
                        val onlineP = Bukkit.getPlayer(entry.key)!!
                        for (command in magenta.mmConfig.getConfig().getStringList("mm_rewards.$entityName.RewardCommands.$index")) {
                            Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                command.replace("%player%", onlineP.name).replace("{player}", onlineP.name)
                            )
                        }
                    }
                }
                damageMap.clear()
            }
        }
    }

    private fun roundingPersonalDamage(damage: Double): Double {
        return damage.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }
    private fun roundingDamage(position: Int): Double {
        return damageByRank(position).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }

    private fun damageByRank(position: Int): Double {
        val topPlayer = sortedByDamage()
        return if (position in 1..topPlayer.size) {
            topPlayer.values.elementAt(position - 1)
        } else {
            0.0
        }
    }

    private fun nameByRank(position: Int): String {
        val topPlayer = sortedByDamage()
        return if (position in 1..topPlayer.size) {
            val uuid = topPlayer.keys.elementAt(position - 1)
            Bukkit.getOfflinePlayer(uuid).name ?: "UNKNOWN"
        } else {
            "EMPTY"
        }
    }

    private fun sortedByDamage(): Map<UUID, Double> {
        return damageMap.toList().sortedByDescending { (_, damage) -> damage }.toMap()
    }

}