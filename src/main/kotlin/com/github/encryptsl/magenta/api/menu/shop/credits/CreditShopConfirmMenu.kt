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

class CreditShopConfirmMenu(private val magenta: Magenta) {
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }

    fun openConfirmMenu(player: Player, creditShop: CreditShop, type: String, creditShopInventory: CreditShopInventory, product: Component, price: Double, quantity: Int, commands: MutableList<String>, isBuyAllowed: Boolean) {
        val gui: Gui = menuUI.simpleGui(
            ModernText.miniModernText(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.gui.display").toString(), Placeholder.component("item", product)),
            magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        menuUI.useAllFillers(gui.filler, magenta.creditShopConfirmMenuConfig.getConfig())

        confirmPay(player, type, gui, creditShop, creditShopInventory, product, price, quantity, commands, isBuyAllowed)
        cancelPay(player, type, gui, creditShop)
        close(player, gui, magenta.creditShopConfirmMenuConfig.getConfig())

        gui.open(player)
    }

    private fun confirmPay(player: Player, type: String, gui: Gui, creditShop: CreditShop, creditShopInventory: CreditShopInventory, product: Component, price: Double, quantity: Int, commands: MutableList<String>, isBuyAllowed: Boolean) {
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
                    creditShopInventory.buyItem(action, product, price, quantity, commands, "magenta.shop.success.buy", isBuyAllowed)
                    creditShop.openCategory(player, type)
                }
            }
            gui.setItem(slot, guiItem)
        }
    }

    private fun cancelPay(player: Player, type: String, gui: Gui, creditShop: CreditShop) {
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
                    creditShop.openCategory(player, type)
                }
            }
            gui.setItem(slot, guiItem)
        }
    }

    private fun close(player: Player, gui: Gui, fileConfiguration: FileConfiguration) {
        for (material in Material.entries) {
            menuUI.closeButton(player, material, gui, fileConfiguration, null)
        }
    }

}