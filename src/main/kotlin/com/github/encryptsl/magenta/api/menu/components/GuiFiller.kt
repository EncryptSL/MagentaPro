package com.github.encryptsl.magenta.api.menu.components

import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


class GuiFiller(
    val row: Int,
    val container: GuiContainer<Player, ItemStack>,
    val config: FileConfiguration
) {
    fun fillBorder() {
        if (config.contains("menu.gui.fill.border")) {
            if (this.getRows() <= 2) {
                return
            }
            for (i in 0..< getRows() * 9) {
                if (i > 8 && (i < this.getRows() * 9 - 8 || i > this.getRows() * 9 - 2) && i % 9 != 0 && i % 9 != 8) continue
                container.set(i, ItemBuilder.from(Material.valueOf(config.getString("menu.gui.fill.border").toString())
                ).asGuiItem { p, _ -> })
            }
        }
    }

    fun fillTop() {
        if (config.contains("menu.gui.fill.top")) {
            for (i in 0..8) {
                container.set(i,
                    ItemBuilder.from(Material.valueOf(config.getString("menu.gui.fill.top").toString()))
                        .asGuiItem { p, _ -> })
            }
        }
    }
    /*
    fun fillBottom() {
        if (config.contains("menu.gui.fill.bottom")) {
            try {
                var i = 9
                while (-1 < i) {
                    if (this.getRows() * 9 - i > 53 && (this.getRows() * 9 - i > 32) && (this.getRows() * 9 - i > 35)) {
                        --i
                        continue
                    }
                    container[getRows() * 9 - i] = ItemBuilder.from(
                        Material.valueOf(
                            (config.getString("menu.gui.fill.bottom").toString())
                        )
                    ).asGuiItem(GuiFiller::`fillBottom$lambda$2`)
                    --i
                }
            } catch (arrayIndexOutOfBoundsException: ArrayIndexOutOfBoundsException) {
                // empty catch block
            }
        }
    }*/

    private fun getRows(): Int {
        return if (!(if (1 <= this.row) this.row < 7 else false)) 1 else this.row
    }
}