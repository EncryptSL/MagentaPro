package com.github.encryptsl.magenta.common.model

import com.github.encryptsl.magenta.Magenta
import java.util.*

class EarnBlocksProgressManager(private val magenta: Magenta) {

    val earnBlocksProgress: WeakHashMap<UUID, Int> = WeakHashMap()

    fun getValue(uuid: UUID): Int = earnBlocksProgress.getOrDefault(uuid, 0)

    fun syncInitData(uuid: UUID, value: Int) {
        earnBlocksProgress.putIfAbsent(uuid, value)
    }

    fun updateProgress(uuid: UUID, value: Int): Int {
        val progress = earnBlocksProgress.computeIfPresent(uuid) {_, v -> v + value} ?: 0

        return progress
    }

    fun save(uuid: UUID) {
        if (!earnBlocksProgress.containsKey(uuid)) return
        val user = magenta.user.getUser(uuid)
        user.set("mined.blocks", getValue(uuid))
    }

    fun saveMinedBlocks() {
        if (earnBlocksProgress.isEmpty()) return
        for (el in earnBlocksProgress.keys) { save(el) }
        clear()
    }

    fun remove(uuid: UUID) {
        earnBlocksProgress.remove(uuid)
    }

    private fun clear() {
        earnBlocksProgress.clear()
    }
}