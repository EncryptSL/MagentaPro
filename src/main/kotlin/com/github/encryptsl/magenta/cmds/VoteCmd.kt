package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ItemBuilder
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.api.shop.helpers.ShopUI
import com.github.encryptsl.magenta.common.hook.nuvotifier.VoteHelper
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class VoteCmd(val magenta: Magenta) {

    private val shopUI: ShopUI by lazy { ShopUI(magenta) }

    @Command("vote")
    @Permission("magenta.vote")
    fun onVote(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)

        val services: List<String> = magenta.config.getConfigurationSection("votifier.services")?.getKeys(false)
            ?.filter { service -> !service.contains("default") } ?: return
        services.forEach { service ->
            val replace = VoteHelper.replaceService(service, "_", ".")
            SchedulerMagenta.delayedTask(magenta, {
                player.sendMessage(ModernText.miniModernText(magenta.config.getString("votifier.services.$service.link").toString(), TagResolver.resolver(
                    Placeholder.parsed("hover", magenta.localeConfig.getMessage("magenta.command.vote.hover")),
                    Placeholder.parsed("vote", (user.getVotesByService(replace)).toString()),
                    Placeholder.parsed("username", player.name)
                )))
            }, 10)
        }
    }

    @Command("vote milestones")
    @Permission("magenta.vote.milestones")
    fun onVoteMilestones(player: Player) {
        openMilestonesGui(player)
    }

    @Command("vote claim rewards")
    @Permission("magenta.vote.claim.rewards")
    fun onVoteClaimRewards(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)
        if (!user.getAccount().contains("votifier.rewards"))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.error.not.reward")))

        SchedulerMagenta.doSync(magenta) {
            VoteHelper.giveRewards(user.getVotifierRewards(), player.name)
        }
        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.vote.success.claim.rewards")))
        user.set("votifier.rewards", null)
    }

    private fun openMilestonesGui(player: Player) {
        if (!magenta.config.contains("votifier.milestones-gui") && !magenta.config.contains("votifier.cumulative"))
            return player.sendMessage(ModernText.miniModernText("<red>Milníky nejsou nastavené !"))

        val playerVotes = magenta.vote.getPlayerVote(player.uniqueId)
        val gui = shopUI.paginatedGui(ModernText.miniModernText(magenta.config.getString("votifier.milestones-gui.title") ?: player.name), 6)

        if (magenta.config.contains("votifier.milestones-gui.items")) {
            for (item in magenta.config.getConfigurationSection("votifier.milestones-gui.items")?.getKeys(false)!!) {
                val material = Material.getMaterial(magenta.config.getString("votifier.milestones-gui.items.$item.item").toString())
                if (material != null) {
                    val itemStack = ItemBuilder(material, 1).setName(ModernText.miniModernText(magenta.config.getString("votifier.milestones-gui.items.$item.name").toString()))
                    val requiredVotes = magenta.config.getInt("votifier.milestones-gui.items.$item.required_votes").minus(playerVotes)
                    val unlockedLores = magenta.config.getStringList("votifier.milestones-gui.items.$item.unlocked-lore").map { ModernText.miniModernText(it) }.toMutableList()
                    val lockedLores = magenta.config.getStringList("votifier.milestones-gui.items.$item.locked-lore").map { ModernText.miniModernText(it, Placeholder.parsed("required_votes", requiredVotes.toString())) }.toMutableList()

                    if (magenta.config.getInt("votifier.milestones-gui.items.$item.required_votes") > playerVotes) {
                        itemStack.addLore(lockedLores)
                    } else {
                        itemStack.addLore(unlockedLores)
                        itemStack.setGlowing(true)
                    }
                    val guiItem = dev.triumphteam.gui.builder.item.ItemBuilder.from(itemStack.create()).asGuiItem()
                    gui.addItem(guiItem)
                }
            }
        }

        controlButtons(player, magenta.config, gui)
        gui.open(player)
    }

    private fun controlButtons(player: Player, fileConfiguration: FileConfiguration, gui: PaginatedGui) {
        for (material in Material.entries) {
            previousPage(fileConfiguration, material, gui)
            closeButton(player, fileConfiguration, material, gui)
            nextPage(fileConfiguration, material, gui)
        }
    }

    private fun previousPage(fileConfiguration: FileConfiguration, material: Material, gui: PaginatedGui) {
        if (fileConfiguration.getString("votifier.milestones-gui.gui.button.previous.item").equals(material.name, true)) {
            gui.setItem(fileConfiguration.getInt("votifier.milestones-gui.gui.button.previous.positions.row"),
                fileConfiguration.getInt("votifier.milestones-gui.gui.button.previous.positions.col"),
                dev.triumphteam.gui.builder.item.ItemBuilder.from(ItemBuilder(material, 1).setName(ModernText.miniModernText(fileConfiguration.getString("votifier.milestones-gui.gui.button.previous.name").toString())).create())
                    .asGuiItem { gui.next() }
            )
        }
    }

    private fun nextPage(fileConfiguration: FileConfiguration, material: Material, gui: PaginatedGui) {
        if (fileConfiguration.getString("votifier.milestones-gui.gui.button.next.item").equals(material.name, true)) {
            gui.setItem(fileConfiguration.getInt("votifier.milestones-gui.gui.button.next.positions.row"),
                fileConfiguration.getInt("votifier.milestones-gui.gui.button.next.positions.col"),
                dev.triumphteam.gui.builder.item.ItemBuilder.from(ItemBuilder(material, 1).setName(ModernText.miniModernText(fileConfiguration.getString("votifier.milestones-gui.gui.button.next.name").toString())).create())
                    .asGuiItem { gui.next() }
            )
        }
    }

    private fun closeButton(player: Player, fileConfiguration: FileConfiguration, material: Material, gui: PaginatedGui) {
        if (fileConfiguration.getString("votifier.milestones-gui.gui.button.close.item").equals(material.name, true)) {
            gui.setItem(fileConfiguration.getInt("votifier.milestones-gui.gui.button.close.positions.row"),
                fileConfiguration.getInt("votifier.milestones-gui.gui.button.close.positions.col"),
                dev.triumphteam.gui.builder.item.ItemBuilder.from(ItemBuilder(material, 1).setName(ModernText.miniModernText(fileConfiguration.getString("votifier.milestones-gui.gui.button.close.name").toString())).create())
                    .asGuiItem { gui.close(player) }
            )
        }
    }
}