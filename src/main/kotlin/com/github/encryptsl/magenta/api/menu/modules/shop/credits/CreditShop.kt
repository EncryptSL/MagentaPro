package com.github.encryptsl.magenta.api.menu.modules.shop.credits

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.config.UniversalConfig
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.glow
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.components.template.Menu
import com.github.encryptsl.magenta.common.Permissions
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.entity.Player

class CreditShop(private val magenta: Magenta) : Menu {


    private val creditShopPaymentMethod: CreditShopPaymentMethod by lazy { CreditShopPaymentMethod(magenta) }
    private val menuUI: MenuUI by lazy { MenuUI(magenta) }
    private val confirmMenu: CreditShopConfirmMenu by lazy { CreditShopConfirmMenu(magenta, menuUI) }

    override fun open(player: Player) {
        val rows = magenta.creditShopConfig.getConfig().getInt("menu.gui.size", 6)
        val gui = Gui.of(rows).title(
            ModernText.miniModernText(magenta.creditShopConfig.getConfig().getString("menu.gui.display").toString())
        )

        gui.component { component ->
            component.render { container, viewer ->
                menuUI.useAllFillers(rows, container, magenta.creditShopConfig.getConfig())

                for (category in magenta.creditShopConfig.getConfig().getConfigurationSection("menu.categories")?.getKeys(false)!!) {
                    val material = Material.getMaterial(
                        magenta.creditShopConfig.getConfig().getString("menu.categories.$category.icon").toString()
                    ) ?: continue

                    if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.name"))
                        return@render viewer.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.name", Placeholder.parsed("category", category))
                        )

                    if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.slot"))
                        return@render viewer.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.slot", Placeholder.parsed("category", category))
                        )
                    if (!magenta.creditShopConfig.getConfig().contains("menu.categories.$category.icon"))
                        return@render viewer.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.icon", Placeholder.parsed("category", category))
                        )

                    val nameString = magenta.creditShopConfig.getConfig().getString("menu.categories.$category.name").toString()
                    val glowing = magenta.creditShopConfig.getConfig().getBoolean("menu.categories.$category.glowing")

                    val item = ItemBuilder.from(
                        createItem(material) {
                            amount = 1
                            meta {
                                setNameComponent = ModernText.miniModernText(nameString)
                                glow = glowing
                            }
                        }
                    ).asGuiItem { whoClick, _ ->
                        openCategory(whoClick, category)
                    }
                    container.set(magenta.creditShopConfig.getConfig().getInt("menu.categories.$category.slot"), item)
                }
            }
        }.build().open(player)
    }


    fun openCategory(player: Player, category: String) {
        val shopCategory = UniversalConfig("${magenta.dataFolder}/menu/creditshop/categories/$category.yml")
        if (!shopCategory.exists())
            return player.sendMessage(
                magenta.locale.translation("magenta.command.shop.error.category.not.exist", Placeholder.parsed("category", category))
            )

        if (!player.hasPermission(Permissions.CREDIT_SHOP_CATEGORY.format(category)) || !player.hasPermission(Permissions.CREDIT_SHOP_CATEGORY_ALL))
            return player.sendMessage(
                magenta.locale.translation("magenta.command.shop.error.category.permission", Placeholder.parsed("category", category))
            )

        val name = magenta.creditShopConfig.getConfig().getString("menu.gui.categoryName").toString()
        val categoryName = magenta.creditShopConfig.getConfig().getString("menu.categories.$category.name").toString()


        val rows = shopCategory.getConfig().getInt("menu.gui.size", 6)

        val gui = Gui.of(rows).title(
            ModernText.miniModernText(name, Placeholder.parsed("category", categoryName)),
        )

        if (!shopCategory.getConfig().contains("menu.items")) return

        gui.component { component ->
            component.render { container, _ ->
                if (shopCategory.getConfig().contains("menu.custom-items")) {
                    menuUI.customItems(player, categoryName, shopCategory.getConfig(), container)
                }
            }
            component.render { container, _ ->
                menuUI.useAllFillers(rows, container, shopCategory.getConfig())
            }
            component.render { container, _ ->
                for (item in shopCategory.getConfig().getConfigurationSection("menu.items")?.getKeys(false)!!) {
                    if (!shopCategory.getConfig().contains("menu.items.$item")) continue
                    val material = Material.getMaterial(shopCategory.getConfig().getString("menu.items.$item.icon").toString()) ?: continue

                    if (!shopCategory.getConfig().contains("menu.items.$item.name"))
                        return@render player.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.name", Placeholder.parsed("category", category))
                        )

                    if (!shopCategory.getConfig().contains("menu.items.$item.position.slot"))
                        return@render player.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.slot", Placeholder.parsed("category", category))
                        )

                    if (!shopCategory.getConfig().contains("menu.items.$item.commands"))
                        return@render player.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.commands", Placeholder.parsed("category", category))
                        )

                    if (!shopCategory.getConfig().contains("menu.items.$item.buy.quantity"))
                        return@render player.sendMessage(
                            magenta.locale.translation("magenta.menu.error.not.defined.quantity", Placeholder.parsed("category", category))
                        )

                    val itemName = shopCategory.getConfig().getString("menu.items.$item.name").toString()
                    val slot = shopCategory.getConfig().getInt("menu.items.$item.position.slot")
                    val hasOptions = shopCategory.getConfig().contains("menu.items.$item.options")
                    val glowing = shopCategory.getConfig().getBoolean("menu.items.$item.options.glowing")
                    val isPotion = shopCategory.getConfig().getBoolean("menu.items.$item.options.potion")
                    val hasColor = shopCategory.getConfig().contains("menu.items.$item.options.color")
                    val color = shopCategory.getConfig().getInt("menu.items.$item.options.color")
                    val buyPrice = shopCategory.getConfig().getDouble("menu.items.$item.buy.price")
                    val quantity = shopCategory.getConfig().getInt("menu.items.$item.buy.quantity")
                    val isBuyAllowed = shopCategory.getConfig().contains("menu.items.$item.buy.price")

                    val itemStack = magenta.itemFactory
                        .creditShopItem(
                            player, material, itemName, quantity, buyPrice, glowing, hasOptions, isPotion, hasColor, color,
                            magenta.creditShopConfig.getConfig().getStringList("menu.gui.item_lore"
                        )
                    )
                    val guiItem = ItemBuilder.from(itemStack).asGuiItem { p, _ ->
                        if (!magenta.creditShopConfig.getConfig().getBoolean("menu.gui.confirm_required")) {
                            return@asGuiItem creditShopPaymentMethod.buy(
                                p, itemStack, itemStack.displayName(), item, shopCategory.getConfig(), isBuyAllowed
                            )
                        }
                        return@asGuiItem confirmMenu.openConfirmMenu(
                            player = p,
                            item = item,
                            category = category,
                            categoryConfig = shopCategory.getConfig(),
                            creditShopPaymentMethod = creditShopPaymentMethod,
                            displayName =  itemStack.displayName(),
                            creditShop = this,
                            isBuyAllowed = isBuyAllowed
                        )
                    }
                    container.set(slot, guiItem)
                }
            }
        }.build().open(player)
    }
}