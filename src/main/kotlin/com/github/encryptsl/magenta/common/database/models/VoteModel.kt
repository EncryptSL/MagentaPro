package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.sql.VoteSQL
import com.github.encryptsl.magenta.common.database.tables.VoteTable
import com.github.encryptsl.magenta.common.database.tables.VoteTable.last_vote
import com.github.encryptsl.magenta.common.database.tables.VoteTable.serviceName
import com.github.encryptsl.magenta.common.database.tables.VoteTable.uuid
import com.github.encryptsl.magenta.common.database.tables.VoteTable.vote
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class VoteModel(private val plugin: Plugin) : VoteSQL {
    override fun createAccount(voteImpl: VoteEntity) {
        SchedulerMagenta.doAsync(plugin) {
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

    override fun hasAccount(uuid: UUID): Boolean = transaction {
        !VoteTable.select(VoteTable.uuid)
            .where(VoteTable.uuid eq uuid.toString())
            .empty()
    }

    override fun hasAccount(uuid: UUID, serviceName: String): Boolean = transaction {
        !VoteTable.select(VoteTable.uuid, VoteTable.serviceName)
            .where((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName))
            .empty()
    }


    override fun addVote(voteImpl: VoteEntity) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                VoteTable.update({ (uuid eq voteImpl.uuid.toString()) and (serviceName eq voteImpl.serviceName) }) {
                    it[vote] = vote.plus(voteImpl.vote)
                    it[last_vote] = voteImpl.lastVote
                }
            }
        }
    }

    override fun setVote(uuid: UUID, serviceName: String, amount: Int) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = amount
                }
            }
        }
    }

    override fun removeVote(uuid: UUID, serviceName: String, amount: Int) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = vote.minus(amount)
                }
            }
        }
    }

    override fun getPlayerVote(uuid: UUID): Int = transaction {
        val user = VoteTable.select(VoteTable.uuid, vote).where(VoteTable.uuid eq uuid.toString())

        return@transaction  user.groupBy(vote).sumOf { row -> row[vote] }
    }

    override fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity? = transaction {
        val user = VoteTable.select(VoteTable.username, VoteTable.uuid, vote, VoteTable.serviceName, last_vote)
            .where((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName))
            .firstOrNull()

        return@transaction user?.let { VoteEntity(it[VoteTable.username], UUID.fromString(it[VoteTable.uuid]), it[vote], it[VoteTable.serviceName], it[last_vote]) }
    }

    override fun removeAccount(uuid: UUID) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                VoteTable.deleteWhere { (VoteTable.uuid eq uuid.toString()) }
            }
        }
    }

    override fun resetVotes(uuid: UUID) {
        SchedulerMagenta.doAsync(plugin) {
            transaction {
                VoteTable.update({ VoteTable.uuid eq uuid.toString() }) {
                    it[vote] = 0
                }
            }
        }
    }

    override fun resetVotes() {
        SchedulerMagenta.doAsync(plugin) {
            transaction { VoteTable.selectAll().forEach {
                it[vote] = 0
            } }
        }
    }

    override fun deleteAll() {
        SchedulerMagenta.doAsync(plugin) {
            transaction { VoteTable.deleteAll() }
        }
    }
    override fun totalVotes(): Int = transaction {
        VoteTable.selectAll().sumOf { row -> row[vote] }
    }

    override fun topVotes(): MutableMap<String, Int> = transaction {
        VoteTable.selectAll().orderBy(vote, SortOrder.DESC).associate {
            it[uuid] to getPlayerVote(UUID.fromString(it[uuid]))
        }.toMutableMap()
    }

}