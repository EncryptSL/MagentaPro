package com.github.encryptsl.magenta.api.menu.ores

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.shop.helpers.ShopUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class OresMilestonesGUI(private val magenta: Magenta) {

    private val shopUI: ShopUI by lazy { ShopUI(magenta) }
    fun openMilestonesOresGUI(player: Player) {
        val gui = shopUI.simpleGui("<dark_gray><underlined>Orečka Postup", 6, GuiType.CHEST)

        for (material in Material.entries) {
            if (magenta.config.contains("level.ores.${material.name}")) {
                val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)
                itemStack.setName(ModernText.miniModernText("<yellow>${material.name}"))
                val requiredLevel = magenta.config.getInt("level.ores.${material.name}")
                if (magenta.levelModel.getLevel(player.uniqueId).level < magenta.config.getInt("level.ores.${material.name}")) {
                    itemStack.addLore(mutableListOf(
                        ModernText.miniModernText("<gray>Informace"),
                        ModernText.miniModernText(" "),
                        ModernText.miniModernText("<gold>Potřebný Level <green><level>", Placeholder.parsed("level", requiredLevel.toString())),
                        ModernText.miniModernText("<white>Tuto rudu momentálně nemůžeš těžit !"),
                        ModernText.miniModernText("<yellow>Stav <red>Uzamčeno")
                    ))
                } else {
                    itemStack.addLore(mutableListOf(
                        ModernText.miniModernText("<gray>Informace"),
                        ModernText.miniModernText(" "),
                        ModernText.miniModernText("<white>Tutu rudu můžeš nyní těžit !"),
                        ModernText.miniModernText("<yellow>Stav <green>Odemčeno")
                    ))
                    itemStack.setGlowing(true)
                }
                val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem()
                gui.addItem(guiItem)
            }
        }
        gui.open(player)
    }

}