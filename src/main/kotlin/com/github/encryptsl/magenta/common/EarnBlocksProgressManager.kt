package com.github.encryptsl.magenta.common

import com.github.encryptsl.magenta.Magenta
import java.util.*

class EarnBlocksProgressManager(private val magenta: Magenta) {

    val earnBlocksProgress: WeakHashMap<UUID, Int> = WeakHashMap()

    fun getValue(uuid: UUID): Int = earnBlocksProgress.getOrDefault(uuid, 0)

    fun saveMinedBlocks() {
        for (el in earnBlocksProgress) {
            val user = magenta.user.getUser(el.key)
            user.set("mined.blocks", el.value)
        }
        clear()
    }

    fun remove(uuid: UUID) {
        earnBlocksProgress.remove(uuid)
    }

    fun clear() {
        earnBlocksProgress.clear()
    }
}