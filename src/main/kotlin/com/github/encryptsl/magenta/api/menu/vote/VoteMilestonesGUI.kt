package com.github.encryptsl.magenta.api.menu.vote

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.ItemBuilder
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class VoteMilestonesGUI(private val magenta: Magenta) {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    fun openVoteMilestonesGui(player: Player) {
        if (!magenta.config.contains("votifier.milestones-gui") && !magenta.config.contains("votifier.cumulative"))
            return player.sendMessage(ModernText.miniModernText("<red>Milníky nejsou nastavené !"))

        val playerVotes = magenta.vote.getPlayerVote(player.uniqueId)
        val gui = menuUI.paginatedGui(ModernText.miniModernText(magenta.config.getString("votifier.milestones-gui.gui.title") ?: player.name), 6)

        if (magenta.config.contains("votifier.milestones-gui.items")) {
            for (item in magenta.config.getConfigurationSection("votifier.milestones-gui.items")?.getKeys(false)!!) {
                val material = Material.getMaterial(magenta.config.getString("votifier.milestones-gui.items.$item.item").toString())
                if (material != null) {
                    val itemStack = ItemBuilder(material, 1).setName(ModernText.miniModernText(magenta.config.getString("votifier.milestones-gui.items.$item.name").toString()))
                    val requiredVotes = magenta.config.getInt("votifier.milestones-gui.items.$item.required_votes").minus(playerVotes)
                    val unlockedLores = magenta.config.getStringList("votifier.milestones-gui.items.$item.unlocked-lore").map { ModernText.miniModernText(it) }.toMutableList()
                    val lockedLores = magenta.config.getStringList("votifier.milestones-gui.items.$item.locked-lore")
                        .map { ModernText.miniModernText(it, TagResolver.resolver(
                            Placeholder.parsed("required_votes", requiredVotes.toString()),
                            Placeholder.parsed("expression_reward",
                                magenta.stringUtils.arithmeticExpression(player, magenta.config, "expression-formula",
                                    magenta.config.getInt("votifier.milestones-gui.items.$item.required_votes", 0)
                                )
                            )))
                        }.toMutableList()

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
        for (material in Material.values()) {
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