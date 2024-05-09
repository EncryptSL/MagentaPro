package com.github.encryptsl.magenta.api.menu.modules.milestones

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.provider.templates.MenuExtender
import dev.triumphteam.gui.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class OresMilestonesGUI(private val magenta: Magenta) : MenuExtender {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val paginationMenu = menuUI.PaginationMenu(magenta, this)

    override fun openMenu(player: HumanEntity) {
        val gui = paginationMenu.paginatedGui(
            ModernText.miniModernText(magenta.milestonesOres.getConfig().getString("menu.gui.display").toString()),
            6,
        )

        menuUI.useAllFillers(gui.filler, magenta.milestonesOres.getConfig())

        for (material in Material.entries) {
            if (!magenta.config.contains("level.ores.${material.name}")) continue

            val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)
            itemStack.setName(
                ModernText.miniModernText(magenta.milestonesOres.getConfig().getString("menu.gui.item.display")
                    ?: player.name, Placeholder.parsed("item", material.name)))
            val requiredLevel = magenta.config.getInt("level.ores.${material.name}")
            if (magenta.levelModel.getLevel(player.uniqueId).level < magenta.config.getInt("level.ores.${material.name}")) {
                val lores = magenta.milestonesOres
                    .getConfig()
                    .getStringList("menu.item.locked-lore")
                    .map { ModernText.miniModernText(it, Placeholder.parsed("level", requiredLevel.toString())) }
                    .toMutableList()
                itemStack.addLore(lores)
            } else {
                val lores = magenta.milestonesOres
                    .getConfig()
                    .getStringList("menu.item.unlocked-lore")
                    .map { ModernText.miniModernText(it) }
                    .toMutableList()
                itemStack.addLore(lores)
                itemStack.setGlowing(true)
            }
            val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem()
            gui.addItem(guiItem)
        }

        paginationMenu.paginatedControlButtons(player, magenta.milestonesOres.getConfig(), gui)

        gui.open(player)
    }

}