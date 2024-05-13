package com.github.encryptsl.magenta.api.level

import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import java.util.*

interface LevelAPI {
    fun createAccount(levelEntity: LevelEntity)
    fun addLevel(uuid: UUID, level: Int)
    fun setLevel(uuid: UUID, level: Int)
    fun addExperience(uuid: UUID, experience: Int)
    fun setExperience(uuid: UUID, experience: Int)
    fun hasAccount(uuid: UUID): Boolean
    fun getLevel(uuid: UUID): LevelEntity
    fun getLevels(top: Int): Map<String, Int>
    fun getLevels(): Map<String, Int>
}