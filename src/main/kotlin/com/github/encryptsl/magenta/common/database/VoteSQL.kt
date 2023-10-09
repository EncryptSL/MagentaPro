package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import java.util.*

interface VoteSQL {
    fun createAccount(voteImpl: VoteEntity)
    fun hasAccount(uuid: UUID, serviceName: String): Boolean
    fun addVote(voteImpl: VoteEntity)
    fun getPlayerVote(uuid: UUID): Int
    fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity?

    fun getVotesForParty(): Int
    fun removeAccount(uuid: UUID)
    fun cleanVotes()
    fun cleanAll()

    fun totalVotes(): Int
    fun topVotes(): MutableMap<String, Int>
}