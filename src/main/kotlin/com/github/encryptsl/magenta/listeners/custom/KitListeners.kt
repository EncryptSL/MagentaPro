package com.github.encryptsl.magenta.listeners.custom

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.InfoType
import com.github.encryptsl.magenta.api.events.kit.*
import com.github.encryptsl.magenta.common.CommandHelper
import com.github.encryptsl.magenta.common.Permissions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class KitListeners(private val magenta: Magenta) : Listener {
    private val commandHelper: CommandHelper by lazy { CommandHelper(magenta) }
    @EventHandler
    fun onCreateKit(event: KitCreateEvent) {
        val player = event.player
        val kitName = event.kitName
        val kitDelay = event.delay
        val kitManager = event.kitManager

        try {
            kitManager.createKit(player, kitName, kitDelay)
            player.sendMessage(magenta.locale.translation("magenta.command.kit.success.created", TagResolver.resolver(
                Placeholder.parsed("kit", kitName),
                Placeholder.parsed("delay", kitDelay.toString())
            )))
        } catch (e : Exception) { player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage)) }
    }

    @EventHandler
    fun onKitDelete(event: KitDeleteEvent) {
        val commandSender = event.commandSender
        val kitName = event.kitName

        try {
            magenta.kitManager.deleteKit(kitName)
            commandSender.sendMessage(magenta.locale.translation("magenta.command.kit.success.deleted", Placeholder.parsed("kit", kitName)))
        } catch (e : Exception) { commandSender.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage)) }
    }

    @EventHandler
    fun onKitAdminGive(event: KitGiveEvent) {
        val commandSender = event.commandSender
        val target = event.target
        val kitName = event.kitName
        val kitManager = event.kitManager

        try {
            kitManager.giveKit(target, kitName)
            target.sendMessage(magenta.locale.translation("magenta.command.kit.success.given.self", Placeholder.parsed("kit", kitName)))
            commandSender.sendMessage(magenta.locale.translation("magenta.command.kit.success.given.to", TagResolver.resolver(
                Placeholder.parsed("username", target.name),
                Placeholder.parsed("kit", kitName)
            )))
        } catch (e : Exception) { commandSender.sendMessage(magenta.locale.translation("magenta.exception", Placeholder.parsed("exception", e.message ?: e.localizedMessage))) }
    }

    @EventHandler
    fun onKitList(event: KitInfoEvent) {
        val commandSender = event.commandSender
        val infoType = event.infoType

        when(infoType) {
            InfoType.LIST -> {
                val section = magenta.kitConfig.getConfig().getConfigurationSection("kits") ?: return

                val list = section.getKeys(false).joinToString { s ->
                    magenta.locale.getMessage("magenta.command.kit.success.list.component").replace("<kit>", s)
                }
                commandSender.sendMessage(magenta.locale.translation("magenta.command.kit.success.list",
                    Placeholder.component("kits", ModernText.miniModernText(list))
                ))
            }
            InfoType.INFO -> {
                val kitName = event.kitName ?: return
                if (magenta.kitConfig.getConfig().getConfigurationSection("kits.$kitName") == null)
                    return commandSender.sendMessage(magenta.locale.translation("magenta.command.kit.error.not.exist"))
                commandSender.sendMessage(ModernText.miniModernText("<yellow>Jméno Kitu $kitName"))
                commandSender.sendMessage(ModernText.miniModernText("<yellow>Delay ${magenta.kitConfig.getConfig().getString("kits.$kitName.delay")}"))
                magenta.kitManager.listOfItems(commandSender, kitName)
            }
        }
    }

    @EventHandler
    fun onKitReceive(event: KitReceiveEvent) {
        val player = event.player
        val kitName = event.kitName
        val delay = event.delay
        val kitManager = event.kitManager
        val user = magenta.user.getUser(player.uniqueId)

        val timeLeft: Duration = user.getRemainingCooldown("kits.$kitName")

        if (user.hasDelay("kits.$kitName") && !player.hasPermission(Permissions.KIT_DELAY_EXEMPT))
            return commandHelper.delayMessage(player, "magenta.command.kit.error.delay", timeLeft)

        try {
            if (delay != 0L && delay != -1L || !player.hasPermission(Permissions.KIT_DELAY_EXEMPT)) {
                user.setDelay(Duration.ofSeconds(delay), "kits.$kitName")
            }
            kitManager.giveKit(player, kitName)
            player.sendMessage(magenta.locale.translation("magenta.command.kit.success.given.self", Placeholder.parsed("kit", kitName)))
        } catch (e : Exception) { player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage)) }
    }

}