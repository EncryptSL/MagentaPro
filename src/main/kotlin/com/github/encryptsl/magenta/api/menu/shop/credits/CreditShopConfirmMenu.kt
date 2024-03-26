package com.github.encryptsl.magenta.api.menu.shop.credits

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class CreditShopConfirmMenu(private val magenta: Magenta, private val menuUI: MenuUI) {

    fun openConfirmMenu(
        player: Player,
        item: String,
        category: String,
        categoryConfig: FileConfiguration,
        creditShopInventory: CreditShopInventory,
        displayName: Component,
        creditShop: CreditShop,
        isBuyAllowed: Boolean = false
    ) {
        val gui: Gui = menuUI.simpleGui(
            ModernText.miniModernText(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.gui.display").toString(), Placeholder.component("item", displayName)),
            magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )
        menuUI.useAllFillers(gui.filler, magenta.creditShopConfirmMenuConfig.getConfig())
        menuUI.playClickSound(player, magenta.creditShopConfirmMenuConfig.getConfig())

        confirmPay(player, item, category, gui, creditShop, categoryConfig, creditShopInventory, displayName, isBuyAllowed)
        cancelPay(player, category, gui, creditShop)
        close(player, gui, magenta.creditShopConfirmMenuConfig.getConfig())

        gui.open(player)
    }

    private fun confirmPay(
        player: Player,
        item: String,
        category: String,
        gui: Gui,
        creditShop: CreditShop,
        config: FileConfiguration,
        creditShopInventory: CreditShopInventory,
        displayName: Component,
        isBuyAllowed: Boolean
    ) {
        if (magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok")) {
            val material = Material.getMaterial(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_ok.icon").toString()) ?: return

            val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok.name"))
                return

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok.slot")) return

            val name = magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_ok.name").toString()
            itemStack.setName(ModernText.miniModernText(name))

            val lore = magenta.creditShopConfirmMenuConfig.getConfig().getStringList("menu.confirm_ok.lore")
                .map { ModernText.miniModernText(it) }
                .toMutableList()

            itemStack.addLore(lore)

            val slot = magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.confirm_ok.slot")

            val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem { action ->
                if (action.isLeftClick || action.isRightClick) {
                    menuUI.playClickSound(action.whoClicked, magenta.creditShopConfig.getConfig())
                    creditShopInventory.buyItem(action, config, item, displayName, isBuyAllowed)
                    creditShop.openCategory(player, category)
                    return@asGuiItem
                }
            }
            gui.setItem(slot, guiItem)
        }
    }

    private fun cancelPay(player: Player, category: String, gui: Gui, creditShop: CreditShop) {
        if (magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no")) {
            val material = Material.getMaterial(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_no.icon").toString()) ?: return

            val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no.name")) return

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no.slot")) return

            val name = magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_no.name").toString()
            itemStack.setName(ModernText.miniModernText(name))

            val lore = magenta.creditShopConfirmMenuConfig.getConfig().getStringList("menu.confirm_no.lore")
                .map { ModernText.miniModernText(it) }
                .toMutableList()

            itemStack.addLore(lore)

            val slot = magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.confirm_no.slot")

            val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem { action ->
                if (action.isLeftClick || action.isRightClick) {
                    menuUI.playClickSound(action.whoClicked, magenta.creditShopConfig.getConfig())
                    creditShop.openCategory(player, category)
                }
            }
            gui.setItem(slot, guiItem)
        }
    }

    private fun close(player: Player, gui: Gui, fileConfiguration: FileConfiguration) {
        for (material in Material.entries) {
            menuUI.playClickSound(player, magenta.creditShopConfig.getConfig())
            menuUI.closeButton(player, material, gui, fileConfiguration, null)
        }
    }

}