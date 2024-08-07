package com.github.encryptsl.magenta.api.level

import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import java.util.*
import java.util.concurrent.CompletableFuture

interface Level {
    fun createAccount(levelEntity: LevelEntity)
    fun addLevel(uuid: UUID, level: Int)
    fun setLevel(uuid: UUID, level: Int)
    fun addExperience(uuid: UUID, experience: Int)
    fun setExperience(uuid: UUID, experience: Int)
    fun hasAccount(uuid: UUID): Boolean
    fun getUserByUUID(uuid: UUID): CompletableFuture<LevelEntity>
    fun getLevels(): CompletableFuture<MutableMap<String, Int>>
}