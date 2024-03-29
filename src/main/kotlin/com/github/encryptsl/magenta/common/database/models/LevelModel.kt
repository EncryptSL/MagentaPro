package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import com.github.encryptsl.magenta.common.database.sql.LevelSQL
import com.github.encryptsl.magenta.common.database.tables.LevelTable
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class LevelModel(private val plugin: Plugin) : LevelSQL {
    override fun createAccount(levelEntity: LevelEntity) {
        SchedulerMagenta.doAsync(plugin) {
            transaction { LevelTable.insertIgnore {
                it[username] = levelEntity.username
                it[uuid] = levelEntity.uuid
                it[level] = levelEntity.level
                it[experience] = levelEntity.experience
            } }
        }
    }
    override fun hasAccount(uuid: UUID): Boolean = transaction {
        !LevelTable.select(LevelTable.uuid).where(LevelTable.uuid eq uuid.toString()).empty()
    }

    override fun addLevel(uuid: UUID, level: Int) {
        transaction {
            LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                it[LevelTable.level] = LevelTable.level plus level
            }
        }
    }

    override fun addExperience(uuid: UUID, experience: Int) {
        transaction {
            LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                it[LevelTable.experience] = LevelTable.experience plus experience
            }
        }
    }

    override fun setLevel(uuid: UUID, level: Int) {
        transaction {
            LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                it[LevelTable.level] = level
            }
        }
    }

    override fun setExperience(uuid: UUID, experience: Int) {
        transaction {
            LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                it[LevelTable.experience] = experience
            }
        }
    }

    override fun getLevel(uuid: UUID): LevelEntity = transaction {
        val user = LevelTable.selectAll().where(LevelTable.uuid eq uuid.toString()).first()
        LevelEntity(user[LevelTable.username], user[LevelTable.uuid], user[LevelTable.level], user[LevelTable.experience])
    }

    override fun getLevels(top: Int): MutableMap<String, Int> = transaction {
        LevelTable.selectAll().limit(top).associate {
            it[LevelTable.username] to it[LevelTable.level]
        }.toMutableMap()
    }

    override fun getLevels(): MutableMap<String, Int> = transaction {
        LevelTable.selectAll().associate {
            it[LevelTable.username] to it[LevelTable.level]
        }.toMutableMap()
    }
}