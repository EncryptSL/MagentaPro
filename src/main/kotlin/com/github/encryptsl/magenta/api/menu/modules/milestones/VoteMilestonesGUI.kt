package com.github.encryptsl.magenta.api.menu.modules.milestones

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.builder.item.ItemBuilder
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
class VoteMilestonesGUI(private val magenta: Magenta) : Menu {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    override fun open(player: Player) {
        if (!magenta.milestonesVotePass.getConfig().contains("menu.items") && !magenta.config.contains("votifier.cumulative"))
            return player.sendMessage(ModernText.miniModernText("<red>Milníky nejsou nastavené !"))

        val playerVotes = magenta.vote.getUserVotesByUUID(player.uniqueId).join()
        val rows = magenta.milestonesVotePass.getConfig().getInt("menu.gui.size", 6)

        val gui = menuUI.paginatedBuilderGui(rows,
            ModernText.miniModernText(
                magenta.milestonesVotePass.getConfig().getString("menu.items.gui.display") ?: player.name
            ),
            magenta.milestonesOres.getConfig()
        )
        val menuSection = magenta.milestonesVotePass.getConfig().getConfigurationSection("menu.items")?.getKeys(false) ?: return

        menuUI.useAllFillers(gui, magenta.milestonesOres.getConfig())
        for (item in menuSection.withIndex()) {
            val material = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).firstOrNull {
                    el -> el.key().value().equals(magenta.milestonesVotePass.getConfig().getString("menu.items.$item.item").toString(), true)
            }?.createItemStack()?.type ?: continue

            val itemStack = ItemCreator(material, 1).setName(
                ModernText.miniModernText(magenta.milestonesVotePass.getConfig().getString("menu.items.$item.name").toString())
            )
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
            val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem()
            gui.addItem(guiItem)
        }
        menuUI.pagination(player, gui, magenta.milestonesVotePass.getConfig(), null)
        gui.open(player)
    }
}