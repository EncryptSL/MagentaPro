package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import java.util.*

interface VoteSQL {
    fun createAccount(voteImpl: VoteEntity)
    fun hasAccount(uuid: UUID): Boolean
    fun hasAccount(uuid: UUID, serviceName: String): Boolean
    fun addVote(voteImpl: VoteEntity)

    fun setVote(uuid: UUID, serviceName: String, amount: Int)
    fun removeVote(uuid: UUID, serviceName: String, amount: Int)
    fun getPlayerVote(uuid: UUID): Int
    fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity?

    fun getVotesForParty(): Int
    fun removeAccount(uuid: UUID)

    fun resetVotes(uuid: UUID)

    fun resetVotes()
    fun deleteAll()

    fun totalVotes(): Int
    fun topVotes(): MutableMap<String, Int>
}