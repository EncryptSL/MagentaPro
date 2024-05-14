package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByAdminEvent
import com.github.encryptsl.magenta.api.events.spy.SpyToggleByPlayerEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.LegacyPaperCommandManager

@Suppress("UNUSED")
class SocialSpyCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("socialspy|spy")
    @Permission("magenta.social.spy")
    @CommandDescription("This command enable you social spy")
    fun onToggleSocialSpy(player: Player) {
        magenta.pluginManager.callEvent(SpyToggleByPlayerEvent(player))
    }

    @Command("socialspy|spy <player>")
    @Permission("magenta.social.spy.other")
    @CommandDescription("This command enable to other player social spy")
    fun onToggleSocialSpyOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        magenta.pluginManager.callEvent(SpyToggleByAdminEvent(commandSender, target))
    }

}