package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.sql.VoteSQL
import com.github.encryptsl.magenta.common.database.tables.VoteTable
import com.github.encryptsl.magenta.common.database.tables.VoteTable.last_vote
import com.github.encryptsl.magenta.common.database.tables.VoteTable.serviceName
import com.github.encryptsl.magenta.common.database.tables.VoteTable.uuid
import com.github.encryptsl.magenta.common.database.tables.VoteTable.vote
import fr.euphyllia.energie.model.SchedulerType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.CompletableFuture

class VoteModel : VoteSQL {
    override fun createAccount(voteImpl: VoteEntity) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        val future = CompletableFuture<Boolean>()
        transaction {
            future.completeAsync {
                !VoteTable.select(VoteTable.uuid)
                    .where(VoteTable.uuid eq uuid.toString())
                    .empty()
            }
        }
        return future
    }

    override fun hasAccount(uuid: UUID, serviceName: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        transaction {
            future.completeAsync {
                !VoteTable.select(VoteTable.uuid, VoteTable.serviceName)
                    .where((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName))
                    .empty()
            }
        }
        return future
    }


    override fun addVote(voteImpl: VoteEntity) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                VoteTable.update({ (uuid eq voteImpl.uuid.toString()) and (serviceName eq voteImpl.serviceName) }) {
                    it[vote] = vote.plus(voteImpl.vote)
                    it[last_vote] = voteImpl.lastVote
                }
            }
        }
    }

    override fun setVote(uuid: UUID, serviceName: String, amount: Int) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = amount
                }
            }
        }
    }

    override fun removeVote(uuid: UUID, serviceName: String, amount: Int) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = vote.minus(amount)
                }
            }
        }
    }

    override fun getPlayerVote(uuid: UUID): CompletableFuture<Int> {
        val future = CompletableFuture<Int>()

        transaction {
            val user = VoteTable.select(VoteTable.uuid, vote).where(VoteTable.uuid eq uuid.toString())

            future.complete(user.groupBy(vote).sumOf { row -> row[vote] })
        }
        return future
    }

    override fun getPlayerVote(uuid: UUID, serviceName: String): CompletableFuture<VoteEntity?> {
        val future = CompletableFuture<VoteEntity?>()

        transaction {
            val user = VoteTable.select(VoteTable.username, VoteTable.uuid, vote, VoteTable.serviceName, last_vote)
                .where((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName))
                .firstOrNull()
            future.completeAsync{
                user?.let { VoteEntity(it[VoteTable.username], UUID.fromString(it[VoteTable.uuid]), it[vote], it[VoteTable.serviceName], it[last_vote]) }
            }
        }

        return future
    }


    override fun removeAccount(uuid: UUID) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                VoteTable.deleteWhere { (VoteTable.uuid eq uuid.toString()) }
            }
        }
    }

    override fun resetVotes(uuid: UUID) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction {
                VoteTable.update({ VoteTable.uuid eq uuid.toString() }) {
                    it[vote] = 0
                }
            }
        }
    }

    override fun resetVotes() {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { VoteTable.selectAll().forEach {
                it[vote] = 0
            } }
        }
    }

    override fun deleteAll() {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { VoteTable.deleteAll() }
        }
    }
    override fun totalVotes(): CompletableFuture<Int> {
        val future = CompletableFuture<Int>()
        transaction {
            future.completeAsync {
                VoteTable.selectAll().sumOf { row -> row[vote] }
            }
        }
        return future
    }

    override fun topVotes(): MutableMap<String, Int> = transaction {
        VoteTable.selectAll().orderBy(vote, SortOrder.DESC).associate {
            it[VoteTable.username] to getPlayerVote(UUID.fromString(it[uuid])).join()
        }.toMutableMap()
    }
}