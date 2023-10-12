package com.github.encryptsl.magenta.api.shop

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.hook.vault.VaultHook
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ShopInventory(private val magenta: Magenta, private val vault: VaultHook) {

    fun buyItem(item: ItemStack, isBuyAllowed: Boolean, price: Int, inventory: InventoryClickEvent) {
        val player = inventory.whoClicked as Player
        if (!isBuyAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.buy.disabled")))

        if (ShopHelper.isPlayerInventoryFull(player))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.inventory.full")))

        val fullPrice = ShopHelper.calcPrice(item.amount, price)

        val response = vault.withdraw(player, fullPrice.toDouble())
        if (response.transactionSuccess()) {
            player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.shop.success.buy"),
                    TagResolver.resolver(
                        Placeholder.component("item", item.displayName()),
                        Placeholder.parsed("quantity", item.amount.toString()),
                        Placeholder.parsed("price", fullPrice.toString()),
                    )
                )
            )
            player.inventory.addItem(item)
        }
        player.updateInventory()
    }

    fun sellItem(item: ItemStack, isSellAllowed: Boolean, price: Int, inventory: InventoryClickEvent) {
        val player = inventory.whoClicked as Player
        if (!isSellAllowed)
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.buy.disabled")))

        if (!player.inventory.contains(item.type))
            return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.shop.error.empty.no.item")))

        val fullPrice = ShopHelper.calcPrice(item.amount, price)

        val response = vault.deposite(player, fullPrice.toDouble())
        if (response.transactionSuccess()) {
            player.inventory.removeItem(item)
            player.sendMessage(
                ModernText.miniModernText(
                    magenta.localeConfig.getMessage("magenta.shop.success.sell"),
                    TagResolver.resolver(
                        Placeholder.component("item", item.displayName()),
                        Placeholder.parsed("quantity", item.amount.toString()),
                        Placeholder.parsed("price", fullPrice.toString()),
                    )
                )
            )
        }
        player.updateInventory()
    }

}