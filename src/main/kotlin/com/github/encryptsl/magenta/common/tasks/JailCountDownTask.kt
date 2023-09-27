package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.PlayerAccount
import com.github.encryptsl.magenta.common.PlayerCooldownManager
import com.github.encryptsl.magenta.common.utils.ModernText
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.player.PlayerTeleportEvent
import java.time.Duration

class JailCountDownTask(private val magenta: Magenta) : Runnable {
    override fun run() {
        magenta.server.onlinePlayers.forEach { player ->
            val cooldownManager = PlayerCooldownManager(player.uniqueId, magenta, "jail")
            val timeLeft: Duration = cooldownManager.getRemainingCooldown()
            val playerAccount = PlayerAccount(magenta, player.uniqueId)
            if (cooldownManager.hasCooldown() || playerAccount.getAccount().getBoolean("jailed")) {
                if (cooldownManager.getRemainingCooldown().seconds == 1L) {
                    player.sendMessage("Byl jsi propušten !")
                    val world = playerAccount.getAccount().getString("lastlocation.world-name").toString()
                    val x = playerAccount.getAccount().getDouble("lastlocation.x")
                    val y = playerAccount.getAccount().getDouble("lastlocation.y")
                    val z = playerAccount.getAccount().getDouble("lastlocation.z")
                    val pitch = playerAccount.getAccount().getDouble("lastlocation.pitch")
                    val yaw = playerAccount.getAccount().getDouble("lastlocation.yaw")
                    playerAccount.getAccount().set("jailed", false)
                    playerAccount.save()
                    playerAccount.reload()
                    magenta.schedulerMagenta.runTask(magenta) {
                        player.teleport(Location(Bukkit.getWorld(world), x, y, z, pitch.toFloat(), yaw.toFloat()), PlayerTeleportEvent.TeleportCause.COMMAND)
                    }
                    return
                }
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.15f, 1.15f)
                player.sendMessage(ModernText.miniModernText("Zbývá ti ${timeLeft.seconds}"))
            }
        }
    }
}