package com.github.encryptsl.magenta.api.level

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import java.util.*

class VirtualLevelAPI(private val magenta: Magenta) : LevelAPI {
    override fun createAccount(levelEntity: LevelEntity) {
        if (hasAccount(UUID.fromString(levelEntity.uuid))) return

        magenta.levelModel.createAccount(levelEntity)
    }

    override fun addLevel(uuid: UUID, level: Int) {
        magenta.levelModel.addLevel(uuid, level)
    }

    override fun setLevel(uuid: UUID, level: Int) {
        magenta.levelModel.setLevel(uuid, level)
    }

    override fun addExperience(uuid: UUID, experience: Int) {
        magenta.levelModel.addExperience(uuid, experience)
    }

    override fun setExperience(uuid: UUID, experience: Int) {
        magenta.levelModel.setExperience(uuid, experience)
    }

    override fun hasAccount(uuid: UUID): Boolean {
        return magenta.levelModel.hasAccount(uuid).join()
    }

    override fun getLevel(uuid: UUID): LevelEntity {
        return magenta.levelModel.getLevel(uuid).join()
    }

    override fun getLevels(top: Int): Map<String, Int> {
        return magenta.levelModel.getLevels(top)
    }

    override fun getLevels(): Map<String, Int> {
        return magenta.levelModel.getLevels()
    }
}