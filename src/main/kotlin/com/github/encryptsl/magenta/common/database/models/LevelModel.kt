package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import com.github.encryptsl.magenta.common.database.sql.LevelSQL
import com.github.encryptsl.magenta.common.database.tables.LevelTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*
import java.util.concurrent.CompletableFuture

class LevelModel : LevelSQL {
    override fun createAccount(levelEntity: LevelEntity) {
        Magenta.scheduler.impl.runAsync {
            transaction { LevelTable.insertIgnore {
                it[username] = levelEntity.username
                it[uuid] = levelEntity.uuid
                it[level] = levelEntity.level
                it[experience] = levelEntity.experience
            } }
        }
    }
    override fun hasAccount(uuid: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val boolean = transaction { !LevelTable.select(LevelTable.uuid).where(LevelTable.uuid eq uuid.toString()).empty() }
        future.completeAsync { boolean }
        return future
    }

    override fun addLevel(uuid: UUID, level: Int) {
        Magenta.scheduler.impl.runAsync  {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.level] = LevelTable.level plus level
                }
            }
        }
    }

    override fun addExperience(uuid: UUID, experience: Int) {
        Magenta.scheduler.impl.runAsync  {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.experience] = LevelTable.experience plus experience
                }
            }
        }
    }

    override fun setLevel(uuid: UUID, level: Int) {
        Magenta.scheduler.impl.runAsync {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.level] = level
                }
            }
        }
    }

    override fun setExperience(uuid: UUID, experience: Int) {
        Magenta.scheduler.impl.runAsync {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.experience] = experience
                }
            }
        }
    }

    override fun getLevel(uuid: UUID): CompletableFuture<LevelEntity> {
        val future = CompletableFuture<LevelEntity>()
        val entity = transaction {
           val user = LevelTable.selectAll().where(LevelTable.uuid eq uuid.toString()).first()
           return@transaction LevelEntity(user[LevelTable.username], user[LevelTable.uuid], user[LevelTable.level], user[LevelTable.experience])
        }
        future.completeAsync { entity }
        return future
    }

    override fun getLevels(top: Int):  CompletableFuture<MutableMap<String, Int>> {
        val future = CompletableFuture<MutableMap<String, Int>>()
        val map = transaction {
            LevelTable.selectAll().limit(top).orderBy(LevelTable.level, SortOrder.DESC).associate {
                it[LevelTable.username] to it[LevelTable.level]
            }.toMutableMap()
        }
        future.completeAsync { map }
        return future
    }

    override fun getLevels():  CompletableFuture<MutableMap<String, Int>> {
        val future = CompletableFuture<MutableMap<String, Int>>()
        val map = transaction {
            LevelTable.selectAll().orderBy(LevelTable.level, SortOrder.DESC).associate {
                it[LevelTable.username] to it[LevelTable.level]
            }.toMutableMap()
        }
        future.completeAsync { map }
        return future
    }
}