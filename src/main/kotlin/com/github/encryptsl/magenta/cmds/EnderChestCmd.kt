package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class EnderChestCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }
    @Command("echest|enderchest")
    @Permission("magenta.echest")
    @CommandDescription("This command open own ender chest")
    fun onEnderChest(player: Player) {
        player.openInventory(player.enderChest)
    }

    @Command("echest|enderchest <player>")
    @Permission("magenta.echest.other")
    @CommandDescription("This command open other player ender chest")
    fun onEnderChestOther(
        player: Player,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        player.openInventory(target.enderChest)
    }

}