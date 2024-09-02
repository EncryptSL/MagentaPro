package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.common.database.entity.LevelEntity
import com.github.encryptsl.magenta.common.database.sql.LevelSQL
import com.github.encryptsl.magenta.common.database.tables.LevelTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
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
        CompletableFuture.runAsync {
            transaction { LevelTable.insertIgnore {
                it[username] = levelEntity.username
                it[uuid] = levelEntity.uuid
                it[level] = levelEntity.level
                it[experience] = levelEntity.experience
            } }
        }.join()
    }
    override fun hasAccount(uuid: UUID): CompletableFuture<Boolean> {
        val boolean = transaction { !LevelTable.select(LevelTable.uuid).where(LevelTable.uuid eq uuid.toString()).empty() }
        return CompletableFuture.supplyAsync { boolean }
    }

    override fun addLevel(uuid: UUID, level: Int) {
        CompletableFuture.runAsync  {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.level] = LevelTable.level plus level
                }
            }
        }.join()
    }

    override fun addExperience(uuid: UUID, experience: Int) {
        CompletableFuture.runAsync  {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.experience] = LevelTable.experience plus experience
                }
            }
        }.join()
    }

    override fun setLevel(uuid: UUID, level: Int) {
        CompletableFuture.runAsync {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.level] = level
                }
            }
        }.join()
    }

    override fun setExperience(uuid: UUID, experience: Int) {
        CompletableFuture.runAsync {
            transaction {
                LevelTable.update({LevelTable.uuid eq uuid.toString()}) {
                    it[LevelTable.experience] = experience
                }
            }
        }.join()
    }

    override fun getUserByUUID(uuid: UUID): CompletableFuture<LevelEntity> {
        val future: CompletableFuture<LevelEntity> = CompletableFuture.supplyAsync {
            transaction {
                try {
                    val user = LevelTable.selectAll().where(LevelTable.uuid eq uuid.toString()).first()
                    LevelEntity(user[LevelTable.username], user[LevelTable.uuid], user[LevelTable.level], user[LevelTable.experience])
                } catch (e : ExposedSQLException) {
                    throw RuntimeException("This user not exist")
                }
            }
        }
        return future
    }

    override fun getLevels():  CompletableFuture<MutableMap<String, Int>> {
        val future: CompletableFuture<MutableMap<String, Int>> = CompletableFuture.supplyAsync {
            transaction {
                LevelTable.selectAll().orderBy(LevelTable.level, SortOrder.DESC).associate {
                    it[LevelTable.username] to it[LevelTable.level]
                }.toMutableMap()
            }
        }

        return future
    }
}