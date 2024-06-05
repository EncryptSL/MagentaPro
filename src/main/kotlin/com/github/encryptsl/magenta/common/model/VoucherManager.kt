package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


class VoucherManager(private val magenta: Magenta) {
    fun createItem(commandSender: CommandSender, voucher: String) {
        val rnd = magenta.random

        if (magenta.vouchers.getConfig().contains("vouchers.$voucher"))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.exist",
                Placeholder.parsed("item", voucher)
            ))

        magenta.vouchers.set("vouchers.$voucher.sid", rnd)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.success.created", TagResolver.resolver(
            Placeholder.parsed("item", voucher),
            Placeholder.parsed("sid", rnd.toString())
        )))
    }

    fun setName(commandSender: CommandSender, voucher: String, name: String) {
        if (!isVoucherExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.exist",
                Placeholder.parsed("item", voucher)
            ))

        magenta.vouchers.set("vouchers.$voucher.name", "$name <white>#<sid>")

        commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.success.set.name", TagResolver.resolver(
            Placeholder.parsed("item", voucher),
            Placeholder.parsed("name", name),
        )))
    }

    fun setMaterial(commandSender: CommandSender, voucher: String, material: Material) {
        if (!isVoucherExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.exist",
                Placeholder.parsed("item", voucher)
            ))

        magenta.vouchers.set("vouchers.$voucher.material", material.name)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.success.set.material", TagResolver.resolver(
            Placeholder.parsed("item", voucher),
            Placeholder.parsed("material", material.name),
        )))
    }

    fun setCommand(commandSender: CommandSender, voucher: String, command: String) {
        if (!isVoucherExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.exist",
                Placeholder.parsed("item", voucher)
            ))

        magenta.vouchers.set("vouchers.$voucher.command", command)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.success.set.command", TagResolver.resolver(
            Placeholder.parsed("item", voucher),
            Placeholder.parsed("command", command),
        )))
    }

    fun setLore(commandSender: CommandSender, voucher: String, lores: List<String>) {
        if (!isVoucherExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.exist",
                Placeholder.parsed("item", voucher)
            ))

        magenta.vouchers.set("vouchers.$voucher.lore", lores)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.success.set.lore", TagResolver.resolver(
            Placeholder.parsed("item", voucher),
            Placeholder.parsed("lore", lores.toString()),
        )))
    }

    fun isVoucherExist(voucher: String): Boolean {
        return magenta.vouchers.getConfig().contains("vouchers.$voucher")
    }

    fun isVoucherNameExist(voucher: String): Boolean {
        return magenta.vouchers.getConfig().contains("vouchers.$voucher.name")
    }

    fun isVoucherMaterialSet(voucher: String): Boolean {
        return magenta.vouchers.getConfig().contains("vouchers.$voucher.material")
    }

    fun isVoucherLoreSet(voucher: String): Boolean {
        return magenta.vouchers.getConfig().contains("vouchers.$voucher.lore")
    }

    fun isVoucherCommandsExist(voucher: String): Boolean {
        return magenta.vouchers.getConfig().contains("vouchers.$voucher.command")
    }

    fun isItemVoucherInHand(itemStack: ItemStack, voucher: String): Boolean {
        return itemStack == getVoucher(voucher, itemStack.amount)
    }

    fun getVoucher(voucher: String, count: Int) : ItemStack? {
        val materialName = magenta.vouchers.getConfig().getString("vouchers.$voucher.material").toString()
        val sid = magenta.vouchers.getConfig().getInt("vouchers.$voucher.sid")
        val item = magenta.vouchers.getConfig().getString("vouchers.$voucher.name").toString()
        val lore = magenta.vouchers.getConfig().getStringList("vouchers.$voucher.lore")
        val isEnabledGlowing = magenta.vouchers.getConfig().getBoolean("vouchers.$voucher.glowing")
        val material = Material.getMaterial(materialName)

        val itemBuilder = material?.let {
            ItemCreator(it, count)
                .setName(ModernText.miniModernText(item, Placeholder.parsed("sid", sid.toString())))
                .addLore(lore.map {
                    ModernText.miniModernText(it)
                }.toMutableList()).setGlowing(isEnabledGlowing)
        }

        return itemBuilder?.create()
    }

    fun giveCommandItem(commandSender: CommandSender, voucher: String, count: Int, target: Player) {
        if (!isVoucherExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.exist",
                Placeholder.parsed("item", voucher)
            ))

        if (!isVoucherNameExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.set.name",
                Placeholder.parsed("item", voucher)
            ))

        if (!isVoucherMaterialSet(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.set.material",
                Placeholder.parsed("item", voucher)
            ))

        if (!isVoucherLoreSet(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.set.lore",
                Placeholder.parsed("item", voucher)
            ))

        if (!isVoucherCommandsExist(voucher))
            return commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.error.not.set.command",
                Placeholder.parsed("item", voucher)
            ))


        val itemStack = getVoucher(voucher, count) ?: return

        target.inventory.addItem(itemStack)

        commandSender.sendMessage(magenta.locale.translation("magenta.command.voucher.success.given", TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.component("item", itemStack.displayName()),
            Placeholder.parsed("amount", count.toString())
        )))
    }
}