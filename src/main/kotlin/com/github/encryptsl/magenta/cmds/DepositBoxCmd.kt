package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.AnnotationParser
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.Command
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.CommandDescription
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.Permission
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class DepositBoxCmd(private val magenta: Magenta) : AnnotationFeatures {
    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>,
    ) {
        annotationParser.parse(this)
    }

    @Command("depositbox|trezor claim")
    @Permission("magenta.deposit.box.claim")
    @CommandDescription("This command claim your rewards from deposit box")
    private fun onTakeDepositBoxRewards(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        if (!user.getAccount().contains("depositbox.rewards"))
            return player.sendMessage(magenta.locale.translation("magenta.command.depositbox.error.claim.empty"))

        VoteHelper.giveRewards(user.getDepositBoxRewards(), player.name)
        player.sendMessage(magenta.locale.translation("magenta.command.depositbox.success.claim.rewards"))
        user.set("depositbox.rewards", null)
    }
}