package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.account.PlayerAccount
import com.github.encryptsl.magenta.common.utils.LocationUtils.generateLocation
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.time.Duration


@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RtpCmd(private val magenta: Magenta) {

    @CommandMethod("rtp")
    @CommandPermission("magenta.rtp")
    fun onRtp(player: Player) {
        val playerAccount = PlayerAccount(magenta, player.uniqueId)
        val delay = magenta.config.getLong("rtp-teleport-cooldown")

        val generatedLocation: Location = generateLocation(magenta, player.world)


        val timeLeft = playerAccount.cooldownManager.getRemainingDelay("rtp")
        if (!playerAccount.cooldownManager.hasDelay("rpt")) {
            if (delay != 0L && delay != -1L || !player.hasPermission("magenta.rtp.delay.exempt")) {
                playerAccount.cooldownManager.setDelay(Duration.ofSeconds(delay), "rtp")
            }
            magenta.schedulerMagenta.runTask(magenta) {
                player.teleportAsync(generatedLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept { response ->
                    player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.rtp.success"), TagResolver.resolver(
                                    Placeholder.parsed("x", generatedLocation.x.toString()),
                                    Placeholder.parsed("y", generatedLocation.y.toString()),
                                    Placeholder.parsed("z", generatedLocation.z.toString())
                    )))
                }
            }
        } else {
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.rtp.error.delay"),
                Placeholder.parsed("delay", timeLeft.toSeconds().toString())
            ))
        }
    }

    @CommandMethod("rtp <player>")
    @CommandPermission("magenta.rtp.other")
    fun onRtpOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val generatedLocation: Location = generateLocation(magenta, target.world)

        magenta.schedulerMagenta.runTask(magenta) {
            target.teleportAsync(generatedLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept { response ->
                target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.rtp.success"), TagResolver.resolver(
                    Placeholder.parsed("x", generatedLocation.x.toString()),
                    Placeholder.parsed("y", generatedLocation.y.toString()),
                    Placeholder.parsed("z", generatedLocation.z.toString())
                )))
            }
        }
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.rtp.success.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("x", generatedLocation.x.toString()),
            Placeholder.parsed("y", generatedLocation.y.toString()),
            Placeholder.parsed("z", generatedLocation.z.toString())
        )))
    }

}