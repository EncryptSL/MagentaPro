package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.BaseGui
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
class WarpPlayerEditorGUI(private val magenta: Magenta) {

    enum class ButtonAction { BACK_TO_MENU, SET_WARP, SET_ICON, DELETE_WARP }

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, WarpGUI(magenta), this) }

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openWarpPlayerEditor(player: Player, warpName: String) {
        val rows = magenta.warpEditorConfig.getConfig().getInt("menu.gui.size", 6)

        val gui = menu.simpleBuilderGui(rows,
            ModernText.miniModernText(
                magenta.warpEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("warp", warpName)
            ), magenta.warpEditorConfig.getConfig()
        )

        val buttons = magenta.warpEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false) ?: return

        menu.useAllFillers(gui, magenta.warpEditorConfig.getConfig())

        for (el in buttons) {
            val material = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).firstOrNull {
                magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.$el.icon").equals(it.key().value(), true)
            } ?: return
            if (magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el}")) {
                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el}.name"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el}.slot"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.${el}.icon"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                val slot = magenta.warpEditorConfig.getConfig().getInt("menu.items.buttons.${el}.slot")

                val itemStack = createItem(material.createItemStack()) {
                    amount = 1
                    meta {
                        setNameComponent = ModernText.miniModernText(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el}.name").toString())
                        setLoreComponentList = magenta.warpEditorConfig
                            .getConfig()
                            .getStringList("menu.items.buttons.$el.lore")
                            .map { ModernText.miniModernText(it) }
                            .toMutableList()
                    }
                }

                val actionItems = ItemBuilder.from(itemStack).asGuiItem { context ->
                    editorActionButton(context.whoClicked as Player, warpName, magenta.warpEditorConfig.getConfig(), el, gui)
                }
                gui.setItem(slot, actionItems)
            }
        }
        gui.open(player)
    }

    private fun editorActionButton(
        player: Player,
        warpName: String,
        config: FileConfiguration,
        el: String,
        gui: BaseGui
    ) {
        val action = ButtonAction.valueOf(config.getString("menu.items.buttons.$el.action").toString())

        when(action) {
            ButtonAction.BACK_TO_MENU -> {
                clicked = false
                warpPlayerGUI.open(player)
            }
            ButtonAction.SET_WARP -> {
                clicked = false
                clearIcons(gui)
                magenta.warpModel.moveWarp(player.uniqueId, warpName, player.location)
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.moved", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("x", player.location.x.toInt().toString()),
                    Placeholder.parsed("y", player.location.y.toInt().toString()),
                    Placeholder.parsed("z", player.location.z.toInt().toString())
                )))
            }
            ButtonAction.SET_ICON -> {
                loadIcons(player, gui, warpName, config)
            }
            ButtonAction.DELETE_WARP -> {
                clicked = false
                magenta.warpModel.deleteWarp(player.uniqueId, warpName)
                player.closeInventory()
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.deleted", Placeholder.parsed("warp", warpName)))
            }
        }
    }

    private fun loadIcons(
        player: Player,
        container: BaseGui,
        warpName: String,
        config: FileConfiguration
    ) {
        val icons: Set<String> = config.getStringList("menu.icons").filter { s -> Material.getMaterial(s) != null }.map { it }.toSet()
        val itemName = config.getString("menu.icon.name")
        if (clicked) return

        clicked = true

        for (icon in icons) {
            val lore = config.getStringList("menu.icon.lore")
                .map { ModernText.miniModernText(it, Placeholder.parsed("icon", icon)) }
                .toMutableList()
            setIcon(player, warpName, itemName ?: icon, container, icon, lore)
        }
    }

    private fun setIcon(
        player: Player,
        warpName: String,
        itemName: String,
        gui: BaseGui,
        materialName: String,
        lore: MutableList<Component>
    ) {
        val material = Material.getMaterial(materialName)!!
        gui.addItem(
            ItemBuilder.from(
                ItemCreator(material, 1)
                    .setName(
                        ModernText.miniModernText(itemName,
                            Placeholder.parsed("icon", materialName))
                    ).addLore(lore)
                    .create()
            ).asGuiItem { context ->
                magenta.warpModel.setWarpIcon(context.whoClicked.uniqueId, warpName, materialName)
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.change.icon", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("icon", materialName)
                )))
            }
        )
        gui.update()
    }

    private fun clearIcons(gui: BaseGui) {
        for (i in 10 .. 43) {
            if (!ignoreSlots.contains(i)) {
                gui.removeItem(i)
            }
        }
    }
}