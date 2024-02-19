package com.github.encryptsl.magenta.api.menu.ores

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class OresMilestonesGUI(private val magenta: Magenta) {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    fun openMilestonesOresGUI(player: Player) {
        val gui = menuUI.simpleGui(magenta.oresMenuConfig.getConfig().getString("menu.gui.display").toString(), 6, GuiType.CHEST)


        menuUI.useAllFillers(gui.filler, magenta.oresMenuConfig.getConfig())

        for (material in Material.entries) {
            if (magenta.config.contains("level.ores.${material.name}")) {
                val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)
                itemStack.setName(ModernText.miniModernText(
                    magenta.oresMenuConfig.getConfig().getString("menu.gui.item.display")
                        ?: player.name, Placeholder.parsed("item", material.name)
                ))
                val requiredLevel = magenta.config.getInt("level.ores.${material.name}")
                if (magenta.levelModel.getLevel(player.uniqueId).level < magenta.config.getInt("level.ores.${material.name}")) {
                    val lores = magenta.oresMenuConfig
                        .getConfig()
                        .getStringList("menu.item.locked-lore")
                        .map { ModernText.miniModernText(it, Placeholder.parsed("level", requiredLevel.toString())) }
                        .toMutableList()
                    itemStack.addLore(lores)
                } else {
                    val lores = magenta.oresMenuConfig
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
        }
        gui.open(player)
    }

}