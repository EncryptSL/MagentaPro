package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager
import java.time.Duration

@Suppress("UNUSED")
class HealCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("heal")
    @Permission("magenta.heal")
    @CommandDescription("This command heal yourself")
    fun onHeal(player: Player) {
        val delay = magenta.config.getLong("heal-cooldown")
        val user = magenta.user.getUser(player.uniqueId)

        val timeLeft = user.getRemainingCooldown("heal")

        if (user.hasDelay("heal") && !player.hasPermission("magenta.heal.delay.exempt")) {
            return magenta.commandHelper.delayMessage(player, "magenta.command.heal.error.delay", timeLeft)
        }

        if (delay != 0L && delay != -1L || !player.hasPermission("magenta.heal.delay.exempt")) {
            user.setDelay(Duration.ofSeconds(delay), "heal")
        }

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.heal")))
        player.health = 20.0
        player.foodLevel = 20
    }

    @Command("heal <player>")
    @Permission("magenta.heal.other")
    @CommandDescription("This command heal other player")
    fun onHeal(commandSender: CommandSender, @Argument(value="player", suggestions = "players") target: Player) {
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.heal")))
        target.health = 20.0
        target.foodLevel = 20
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.heal.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name)
        )))
    }

}