package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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

        val gui = Gui
            .of(rows)
            .title(ModernText.miniModernText(
                    magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.gui.display").toString(),
                    Placeholder.component("item", displayName)
            ))

        gui.component { component ->
            component.render { container, _ ->
                menuUI.useAllFillers(rows, container, magenta.creditShopConfirmMenuConfig.getConfig())
            }
            component.render { container, _ ->
                confirmPay(item, category, container, creditShop, categoryConfig, creditShopPaymentMethod, displayName, isBuyAllowed)
                cancelPay(category, container, creditShop)
                //close(player, component, magenta.creditShopConfirmMenuConfig.getConfig())
            }
        }.build().open(player)
    }
    private fun confirmPay(
        item: String,
        category: String,
        container: GuiContainer<Player, ItemStack>,
        creditShop: CreditShop,
        config: FileConfiguration,
        creditShopPaymentMethod: CreditShopPaymentMethod,
        displayName: Component,
        isBuyAllowed: Boolean
    ) {
        if (magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_ok")) {
            val material = Material.getMaterial(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_ok.icon").toString()) ?: return

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

            val guiItem = ItemBuilder.from(itemStack).asGuiItem { player, context ->
                creditShopPaymentMethod.buy(player, itemStack, displayName, item, config, isBuyAllowed)
                creditShop.openCategory(player, category)
                return@asGuiItem
            }
            container.set(slot, guiItem)
        }
    }

    private fun cancelPay(category: String, container: GuiContainer<Player, ItemStack>, creditShop: CreditShop) {
        if (magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no")) {
            val material = Material.getMaterial(magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_no.icon").toString()) ?: return

            val itemStack = com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no.name")) return

            if (!magenta.creditShopConfirmMenuConfig.getConfig().contains("menu.confirm_no.slot")) return

            val name = magenta.creditShopConfirmMenuConfig.getConfig().getString("menu.confirm_no.name").toString()
            itemStack.setName(ModernText.miniModernText(name))

            val lore = magenta.creditShopConfirmMenuConfig.getConfig().getStringList("menu.confirm_no.lore")
                .map { ModernText.miniModernText(it) }
                .toMutableList()

            itemStack.addLore(lore)

            val slot = magenta.creditShopConfirmMenuConfig.getConfig().getInt("menu.confirm_no.slot")

            val guiItem = ItemBuilder.from(itemStack.create()).asGuiItem { player, _ ->
                return@asGuiItem creditShop.openCategory(player, category)
            }
            container.set(slot, guiItem)
        }
    }

    /*
    private fun close(player: HumanEntity, gui: Gui, fileConfiguration: FileConfiguration) {
        for (material in Material.entries) {
            simpleMenu.closeMenuOrBack(player, material, gui, fileConfiguration, null)
        }
    }*/

}