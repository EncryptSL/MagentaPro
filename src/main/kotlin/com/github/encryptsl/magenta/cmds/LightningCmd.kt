package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*

@Suppress("UNUSED")
class LightningCmd(private val magenta: Magenta) {
    @Command("lightning|thor <player>")
    @Permission("magenta.lightning")
    @CommandDescription("This command create lightning effect with dmg or without to player")
    fun onLightning(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player, @Flag(value = "damage", aliases = ["d"]) damage: Boolean) {
        if (damage) target.world.strikeLightning(target.location) else target.world.strikeLightningEffect(target.location)

        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.lightning.success.to"),
            Placeholder.parsed("player", target.name)
        ))
    }
}