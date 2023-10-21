package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class HealCmd(private val magenta: Magenta) {

    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }

    @CommandMethod("heal")
    @CommandPermission("magenta.heal")
    fun onHeal(player: Player) {
        val delay = magenta.config.getLong("heal-cooldown")
        val user = magenta.user.getUser(player.uniqueId)

        val timeLeft = user.cooldownManager.getRemainingDelay("heal")

        if (!user.cooldownManager.hasDelay("heal")) {
            if (delay != 0L && delay != -1L || !player.hasPermission("magenta.heal.delay.exempt")) {
                user.cooldownManager.setDelay(Duration.ofSeconds(delay), "heal")
            }

            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.heal")))
            player.health = 20.0
            player.foodLevel = 20
        } else {
            commandHelper.delayMessage(player, "magenta.command.heal.error.delay", timeLeft)
        }
    }

    @CommandMethod("heal <player>")
    @CommandPermission("magenta.heal.other")
    fun onHeal(commandSender: CommandSender, @Argument(value="player", suggestions = "players") target: Player) {
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.heal")))
        target.health = 20.0
        target.foodLevel = 20
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.heal.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name)
        )))
    }

}