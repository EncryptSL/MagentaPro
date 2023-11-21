package com.github.encryptsl.magenta.api

import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta

class ItemBuilder(private val material: Material, val amount: Int) {

    var name: Component? = null
    private var glowing = false
    private val lore: MutableList<Component> = mutableListOf()
    private val enchants: MutableMap<Enchantment, Int> = mutableMapOf()
    private var isPotion: Boolean = false
    private var potionColor: Color? = null


    fun setPotion(boolean: Boolean, color: Color): ItemBuilder {
        this.isPotion = boolean
        this.potionColor = color
        return this
    }
    fun setGlowing(boolean: Boolean): ItemBuilder {
        this.glowing = boolean
        return this
    }

    fun setName(name: Component): ItemBuilder {
        this.name = name
        return this
    }

    fun addLore(lores: MutableList<Component>): ItemBuilder {
        if (lores.isNotEmpty())
            this.lore.addAll(lores)

        return this
    }

    fun addEnchantment(enchants: Enchantment, level: Int): ItemBuilder {
        this.enchants[enchants] = level

        return this
    }

    fun create(): ItemStack {
        val itemStack = ItemStack(material, amount)
        var itemMeta = itemStack.itemMeta

        if (name != null) {
            itemMeta.displayName(name)
        }

        if (lore.isNotEmpty()) {
            itemMeta.lore(lore)
        }

        if (isPotion && potionColor != null) {
            itemMeta = itemMeta as PotionMeta
            itemMeta.color = potionColor
        }

        if (glowing) {
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        if (enchants.isNotEmpty()) {
            enchants.forEach { (t, u) ->
                itemMeta.addEnchant(t, u, false)
            }
        }

        itemStack.setItemMeta(itemMeta)

        return itemStack
    }


}