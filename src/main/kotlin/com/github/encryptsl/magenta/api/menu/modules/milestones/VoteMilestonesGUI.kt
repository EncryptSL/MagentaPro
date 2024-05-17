package com.github.encryptsl.magenta.api.menu.modules.milestones

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemBuilder
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity

class VoteMilestonesGUI(private val magenta: Magenta) : MenuExtender {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)

    override fun openMenu(player: HumanEntity) {
        if (!magenta.milestonesVotePass.getConfig().contains("menu.items") && !magenta.config.contains("votifier.cumulative"))
            return player.sendMessage(ModernText.miniModernText("<red>Milníky nejsou nastavené !"))

        val playerVotes = magenta.vote.getPlayerVote(player.uniqueId)
        val gui = paginationMenu.paginatedGui(ModernText.miniModernText(magenta.milestonesVotePass.getConfig().getString("menu.items.gui.display") ?: player.name), 6)

        menuUI.useAllFillers(gui.filler, magenta.milestonesVotePass.getConfig())

        val menuSection = magenta.milestonesVotePass.getConfig().getConfigurationSection("menu.items")?.getKeys(false) ?: return

        for (item in menuSection) {
            val material = Material.getMaterial(magenta.milestonesVotePass.getConfig().getString("menu.items.$item.item").toString()) ?: continue
            val itemStack = ItemBuilder(material, 1).setName(ModernText.miniModernText(magenta.milestonesVotePass.getConfig().getString("menu.items.$item.name").toString()))
            val requiredVotes = magenta.milestonesVotePass.getConfig().getInt("menu.items.$item.required_votes").minus(playerVotes)
            val unlockedLores = magenta.milestonesVotePass.getConfig().getStringList("menu.items.$item.unlocked-lore").map { ModernText.miniModernText(it) }.toMutableList()
            val lockedLores = magenta.milestonesVotePass.getConfig().getStringList("menu.items.$item.locked-lore")
                .map { ModernText.miniModernText(it, TagResolver.resolver(
                    Placeholder.parsed("required_votes", requiredVotes.toString()),
                    Placeholder.parsed("expression_reward",
                        magenta.stringUtils.arithmeticExpression(player as OfflinePlayer, magenta.config, "votifier.expression-formula",
                            magenta.milestonesVotePass.getConfig().getInt("menu.items.$item.required_votes", 0)
                        )
                    )))
                }.toMutableList()

            if (magenta.milestonesVotePass.getConfig().getInt("menu.items.$item.required_votes") > playerVotes) {
                itemStack.addLore(lockedLores)
            } else {
                itemStack.addLore(unlockedLores)
                itemStack.setGlowing(true)
            }
            val guiItem = dev.triumphteam.gui.builder.item.ItemBuilder.from(itemStack.create()).asGuiItem()
            gui.addItem(guiItem)
        }

        paginationMenu.paginatedControlButtons(player, magenta.milestonesVotePass.getConfig(), gui)

        gui.open(player)
    }
}