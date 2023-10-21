package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import java.util.*

interface VoteAPI {
    fun addVote(voteImpl: VoteEntity)
    fun setVote(voteImpl: VoteEntity)
    fun removeVote(voteImpl: VoteEntity)
    fun hasAccount(uuid: UUID, serviceName: String): Boolean
    fun getPlayerVote(uuid: UUID): Int

    fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity?

    fun getVotesForParty(): Int
    fun removeAccount(uuid: UUID)
    fun cleanVotes()
    fun cleanAll()
    fun totalVotes(): Int
    fun topVotes(): MutableMap<String, Int>
}