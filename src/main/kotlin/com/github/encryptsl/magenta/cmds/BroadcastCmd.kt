package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class BroadcastCmd(private val magenta: Magenta) {
    @Command("broadcast|oznameni <message>")
    @Permission("magenta.broadcast")
    fun onBroadcast(commandSender: CommandSender, @Greedy @Argument(value = "message") message: String) {
        commandSender.server.broadcast(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.broadcast"),
                Placeholder.parsed("message", message)
            )
        )
    }
}