package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.builder.item.ItemBuilder
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
        creditShopPaymentMethod: CreditShopPaymentMethod,
        displayName: Component,
        creditShop: CreditShop,
        isBuyAllowed: Boolean = false
    ) {
        val rows = magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.gui.size", 6)

        val gui = menuUI.simpleBuilderGui(rows, ModernText.miniModernText(
                magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.component("item", displayName)),
            magenta.creditShopConfirmMenuConfig.getConfig()
        )


        menuUI.useAllFillers(gui, magenta.creditShopConfirmMenuConfig.getConfig())
        confirmPay(item, category, gui, creditShop, categoryConfig, creditShopPaymentMethod, displayName, isBuyAllowed)
        cancelPay(category, gui, creditShop)
        close(player, gui, magenta.creditShopConfirmMenuConfig.getConfig())

        gui.open(player)
    }
    private fun confirmPay(
        item: String,
        category: String,
        gui: Gui,
        creditShop: CreditShop,
        config: FileConfiguration,
        creditShopPaymentMethod: CreditShopPaymentMethod,
        displayName: Component,
        isBuyAllowed: Boolean
    ) {
        if (magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok")) {
            val material = Material.matchMaterial(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_ok.icon").toString()) ?: return

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok.name"))
                return

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok.slot")) return

            val itName = magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_ok.name").toString()

            val lore = magenta.creditShopConfirmMenuConfig.getConfig().getStringList("menu.confirm_ok.lore")
                .map { ModernText.miniModernText(it) }
                .toMutableList()

            val itemStack = createItem(material) {
                amount = 1
                meta {
                    setNameComponent = ModernText.miniModernText(itName)
                    setLoreComponentList = lore
                }
            }

            val slot = magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.confirm_ok.slot")

            val guiItem = ItemBuilder.from(itemStack).asGuiItem { context ->
                creditShopPaymentMethod.buy(context.whoClicked as Player, itemStack, displayName, item, config, isBuyAllowed)
                creditShop.openCategory(context.whoClicked as Player, category)
                return@asGuiItem
            }
            gui.setItem(slot, guiItem)
        }
    }

    private fun cancelPay(category: String, gui: Gui, creditShop: CreditShop) {
        if (magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no")) {
            val material = Material.getMaterial(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_no.icon").toString()) ?: return

            val itemStack = ItemCreator(material, 1)

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no.name")) return

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no.slot")) return

            val name = magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_no.name").toString()
            itemStack.setName(ModernText.miniModernText(name))

            val lore = magenta.creditShopConfirmMenuConfig.getConfig().getStringList("menu.confirm_no.lore")
                .map { ModernText.miniModernText(it) }
                .toMutableList()

            itemStack.addLore(lore)

            val slot = magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.confirm_no.slot")

            val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem { context ->
                return@asGuiItem creditShop.openCategory(context.whoClicked as Player, category)
            }
            gui.setItem(slot, guiItem)
        }
    }

    private fun close(player: Player, gui: Gui, fileConfiguration: FileConfiguration) {
        for (material in Material.entries) {
            menuUI.closeMenuOrBack(player, material, gui, fileConfiguration, null)
        }
    }

}