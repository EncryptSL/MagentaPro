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
import org.bukkit.entity.Player

class OresMilestonesGUI(private val magenta: Magenta) : Menu {

    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    override fun open(player: Player) {
        val rows = magenta.milestonesOres.getConfig().getInt("menu.gui.size", 6)

        val gui = menuUI.simpleBuilderGui(rows, ModernText.miniModernText(
            magenta.milestonesOres.getConfig().getString("menu.gui.display").toString()),
            magenta.milestonesOres.getConfig()
        )

        val getLevel = magenta.levelAPI.getUserByUUID(player.uniqueId).join()
        val section = magenta.config.getConfigurationSection("level.ores")?.getKeys(false) ?: return

        menuUI.useAllFillers(gui, magenta.milestonesOres.getConfig())

        for (key in section.withIndex()) {
            val material = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).firstOrNull {
                el -> key.value.equals(el.key().value(), true)
            }?.createItemStack()?.type ?: continue

            val itemStack = ItemCreator(material, 1).setName(
                ModernText.miniModernText(magenta.milestonesOres.getConfig().getString("menu.gui.item.display")
                    ?: player.name, Placeholder.parsed("item", material.name))
            )
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
            gui.addItem(ItemBuilder.from(itemStack.create()).asGuiItem())
        }
        gui.open(player)
    }
}