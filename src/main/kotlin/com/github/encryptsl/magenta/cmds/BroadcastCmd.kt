package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Greedy
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class BroadcastCmd(private val magenta: Magenta) {
    @CommandMethod("broadcast|oznameni <message>")
    @CommandPermission("magenta.broadcast")
    fun onBroadcast(commandSender: CommandSender, @Greedy @Argument(value = "message") message: String) {
        commandSender.server.broadcast(
            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.broadcast"),
                Placeholder.parsed("message", message)
            )
        )
    }
}