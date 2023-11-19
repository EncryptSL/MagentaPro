package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import java.util.*

interface LevelSQL {

    fun createAccount(levelEntity: LevelEntity)

    fun addLevel(uuid: UUID, level: Int)
    fun setLevel(uuid: UUID, level: Int)
    fun addExperience(uuid: UUID, experience: Int)
    fun setExperience(uuid: UUID, experience: Int)

    fun hasAccount(uuid: UUID): Boolean

    fun getLevel(uuid: UUID): LevelEntity?

    fun getLevels(top: Int): MutableMap<String, Int>
    fun getLevels(): MutableMap<String, Int>
}