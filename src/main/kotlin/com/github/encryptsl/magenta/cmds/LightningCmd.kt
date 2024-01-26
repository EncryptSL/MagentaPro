package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.*
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class LightningCmd(private val magenta: Magenta) {
    @Command("lightning|thor <player> [damage]")
    @Permission("magenta.lightning")
    fun onLightning(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player, @Flag(value = "damage") damage: Boolean) {
        magenta.schedulerMagenta.doSync(magenta) {
            if (damage) {
                target.world.strikeLightning(target.location)
            } else {
                target.world.strikeLightningEffect(target.location)
            }
        }
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.lightning.success.to"),
            Placeholder.parsed("player", target.name)
        ))
    }
}