package com.github.encryptsl.magenta.api.menu.modules.milestones

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemBuilder
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.paper.Gui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player

class VoteMilestonesGUI(private val magenta: Magenta) : Menu {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    override fun open(player: Player) {
        if (!magenta.milestonesVotePass.getConfig().contains("menu.items") && !magenta.config.contains("votifier.cumulative"))
            return player.sendMessage(ModernText.miniModernText("<red>Milníky nejsou nastavené !"))

        val playerVotes = magenta.vote.getPlayerVote(player.uniqueId)
        val rows = magenta.milestonesVotePass.getConfig().getInt("menu.gui.rows", 6)

        val gui = Gui.of(rows).title(
            ModernText.miniModernText(
                magenta.milestonesVotePass.getConfig().getString("menu.items.gui.display") ?: player.name
            )
        )
        val menuSection = magenta.milestonesVotePass.getConfig().getConfigurationSection("menu.items")?.getKeys(false) ?: return

        gui.component { component ->
            component.render { container, _ ->
                menuUI.useAllFillers(rows, container, magenta.milestonesOres.getConfig())
                for (item in menuSection.withIndex()) {
                    val material = Material.getMaterial(magenta.milestonesVotePass.getConfig().getString("menu.items.$item.item").toString()) ?: continue
                    val itemStack = ItemBuilder(material, 1).setName(ModernText.miniModernText(magenta.milestonesVotePass.getConfig().getString("menu.items.$item.name").toString()))
                    val requiredVotes = magenta.milestonesVotePass.getConfig().getInt("menu.items.$item.required_votes").minus(playerVotes)
                    val unlockedLores = magenta.milestonesVotePass.getConfig().getStringList("menu.items.$item.unlocked-lore")
                        .map { ModernText.miniModernText(it) }
                        .toMutableList()
                    val lockedLores = magenta.milestonesVotePass.getConfig().getStringList("menu.items.$item.locked-lore")
                        .map { ModernText.miniModernText(it, TagResolver.resolver(
                            Placeholder.parsed("required_votes", requiredVotes.toString()),
                            Placeholder.parsed("expression_reward",
                                magenta.stringUtils.arithmeticExpression(player, magenta.config, "votifier.expression-formula",
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
                    val guiItem = dev.triumphteam.gui.paper.builder.item.ItemBuilder.from(itemStack.create()).asGuiItem { whoClick, context -> }
                    container.set(item.index, guiItem)
                }
            }
        }.build().open(player)
    }
}