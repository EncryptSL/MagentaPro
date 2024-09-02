package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.sql.VoteSQL
import com.github.encryptsl.magenta.common.database.tables.VoteTable
import com.github.encryptsl.magenta.common.database.tables.VoteTable.last_vote
import com.github.encryptsl.magenta.common.database.tables.VoteTable.serviceName
import com.github.encryptsl.magenta.common.database.tables.VoteTable.username
import com.github.encryptsl.magenta.common.database.tables.VoteTable.uuid
import com.github.encryptsl.magenta.common.database.tables.VoteTable.vote
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.CompletableFuture

class VoteModel : VoteSQL {
    override fun createAccount(voteImpl: VoteEntity) {
        CompletableFuture.runAsync {
            transaction {
                VoteTable.insertIgnore {
                    it[username] = voteImpl.username
                    it[uuid] = voteImpl.uuid.toString()
                    it[vote] = voteImpl.vote
                    it[serviceName] = voteImpl.serviceName
                    it[last_vote] = voteImpl.lastVote
                }
            }
        }
    }

    override fun hasAccount(uuid: UUID): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            transaction { !VoteTable.select(VoteTable.uuid).where(VoteTable.uuid eq uuid.toString()).empty() }
        }
    }

    override fun hasAccount(uuid: UUID, serviceName: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync { transaction {
            !VoteTable
                .select(VoteTable.uuid, VoteTable.serviceName)
                .where((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName))
                .empty()
        } }
    }


    override fun addVote(voteImpl: VoteEntity) {
        CompletableFuture.runAsync {
            transaction {
                VoteTable.update({ (uuid eq voteImpl.uuid.toString()) and (serviceName eq voteImpl.serviceName) }) {
                    it[username] = voteImpl.username
                    it[vote] = vote.plus(voteImpl.vote)
                    it[last_vote] = voteImpl.lastVote
                }
            }
        }
    }

    override fun setVote(uuid: UUID, serviceName: String, amount: Int) {
        CompletableFuture.runAsync {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = amount
                }
            }
        }
    }

    override fun removeVote(uuid: UUID, serviceName: String, amount: Int) {
        CompletableFuture.runAsync {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = vote.minus(amount)
                }
            }
        }
    }

    override fun getUserVotesByUUIDAndService(uuid: UUID, serviceName: String): CompletableFuture<VoteEntity> {
        val future: CompletableFuture<VoteEntity> = CompletableFuture.supplyAsync {
            transaction {
                try {
                    val row = VoteTable.select(username, VoteTable.uuid, vote, VoteTable.serviceName, last_vote)
                        .where((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName)).first()
                    return@transaction VoteEntity(row[username], UUID.fromString(row[VoteTable.uuid]), row[vote], row[VoteTable.serviceName], row[last_vote])
                } catch (e : ExposedSQLException) {
                    throw RuntimeException("Votes by service and uuid not found !")
                }
            }
        }
        return future
    }

    override fun getUserVotesByUUID(uuid: UUID): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync {
            VoteTable.select(VoteTable.uuid, vote).where(VoteTable.uuid eq uuid.toString()).groupBy(vote).sumOf { row -> row[vote] }
        }
    }

    override fun removeAccount(uuid: UUID) {
        CompletableFuture.runAsync {
            transaction {
                VoteTable.deleteWhere { (VoteTable.uuid eq uuid.toString()) }
            }
        }
    }

    override fun resetVotes(uuid: UUID) {
        CompletableFuture.runAsync {
            transaction {
                VoteTable.update({ VoteTable.uuid eq uuid.toString() }) {
                    it[vote] = 0
                }
            }
        }
    }

    override fun resetVotes() {
        CompletableFuture.runAsync {
            transaction { VoteTable.selectAll().forEach {
                it[vote] = 0
            } }
        }
    }

    override fun deleteAll() {
        CompletableFuture.runAsync {
            transaction { VoteTable.deleteAll() }
        }
    }
    override fun totalVotes(): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync { transaction { VoteTable.selectAll().sumOf { row -> row[vote] } } }
    }

    override fun topVotes(): MutableMap<String, Int> = transaction {
        VoteTable.selectAll().groupBy(username).orderBy(vote, SortOrder.DESC).associate {
            it[username] to getUserVotesByUUID(UUID.fromString(it[uuid])).join()
        }.toMutableMap()
    }
}