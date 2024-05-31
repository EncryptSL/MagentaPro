package com.github.encryptsl.magenta.api.menu.modules.milestones

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class OresMilestonesGUI(private val magenta: Magenta) : Menu {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    override fun open(player: Player) {
        val rows = magenta.milestonesOres.getConfig().getInt("menu.gui.size", 6)

        val gui = Gui.of(rows).title(ModernText.miniModernText(
            magenta.milestonesOres.getConfig().getString("menu.gui.display").toString()
        ))

        val getLevel = magenta.virtualLevel.getLevel(player.uniqueId)
        val section = magenta.config.getConfigurationSection("level.ores")?.getKeys(false) ?: return

        gui.component { component ->
            component.render { container, viewer ->
                menuUI.useAllFillers(rows, container, magenta.milestonesOres.getConfig())

                for (key in section.withIndex()) {
                    val material = Material.getMaterial(key.value) ?: continue
                    val itemStack = com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)
                    itemStack.setName(
                        ModernText.miniModernText(magenta.milestonesOres.getConfig().getString("menu.gui.item.display")
                            ?: viewer.name, Placeholder.parsed("item", material.name)))
                    val requiredLevel = magenta.config.getInt("level.ores.${material.name}")

                    if (getLevel.level < magenta.config.getInt("level.ores.${material.name}")) {
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
                    container.set(key.index, ItemBuilder.from(itemStack.create()).asGuiItem { _, _ -> })
                }
            }
        }.build().open(player)
    }
}