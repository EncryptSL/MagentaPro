package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.sql.VoteSQL
import com.github.encryptsl.magenta.common.database.tables.VoteTable
import com.github.encryptsl.magenta.common.database.tables.VoteTable.last_vote
import com.github.encryptsl.magenta.common.database.tables.VoteTable.serviceName
import com.github.encryptsl.magenta.common.database.tables.VoteTable.uuid
import com.github.encryptsl.magenta.common.database.tables.VoteTable.vote
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class VoteModel(private val magenta: Magenta) : VoteSQL {
    override fun createAccount(voteImpl: VoteEntity) {
        magenta.schedulerMagenta.doAsync(magenta) {
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
        !VoteTable.select(VoteTable.uuid eq uuid.toString()).empty()
    }

    override fun hasAccount(uuid: UUID, serviceName: String): Boolean = transaction {
        !VoteTable.select((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName)).empty()
    }


    override fun addVote(voteImpl: VoteEntity) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                VoteTable.update({ (uuid eq voteImpl.uuid.toString()) and (serviceName eq voteImpl.serviceName) }) {
                    it[vote] = vote.plus(voteImpl.vote)
                    it[last_vote] = voteImpl.lastVote
                }
            }
        }
    }

    override fun setVote(uuid: UUID, serviceName: String, amount: Int) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = amount
                }
            }
        }
    }

    override fun removeVote(uuid: UUID, serviceName: String, amount: Int) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                VoteTable.update ({ (VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName) }) {
                    it[vote] = vote.minus(amount)
                }
            }
        }
    }

    override fun getPlayerVote(uuid: UUID): Int = transaction {
        val user = VoteTable.select(VoteTable.uuid eq uuid.toString())

        return@transaction  user.groupBy(vote).sumOf { row -> row[vote] }
    }

    override fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity? = transaction {
        val user = VoteTable.select((VoteTable.uuid eq uuid.toString()) and (VoteTable.serviceName eq serviceName)).firstOrNull()

        return@transaction user?.let { VoteEntity(it[VoteTable.username], UUID.fromString(it[VoteTable.uuid]), it[vote], it[VoteTable.serviceName], it[last_vote]) }
    }

    override fun removeAccount(uuid: UUID) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                VoteTable.deleteWhere { (VoteTable.uuid eq uuid.toString()) }
            }
        }
    }

    override fun resetVotes(uuid: UUID) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                VoteTable.update({ VoteTable.uuid eq uuid.toString() }) {
                    it[vote] = 0
                }
            }
        }
    }

    override fun resetVotes() {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { VoteTable.selectAll().forEach {
                it[vote] = 0
            } }
        }
    }

    override fun deleteAll() {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { VoteTable.deleteAll() }
        }
    }
    override fun totalVotes(): Int = transaction {
        VoteTable.selectAll().sumOf { row -> row[vote] }
    }

    override fun topVotes(): MutableMap<String, Int> = transaction {
        VoteTable.selectAll().associate {
            it[uuid] to getPlayerVote(UUID.fromString(it[uuid]))
        }.toMutableMap()
    }

}