package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import java.util.*

interface VoteAPI {
    fun addVote(voteImpl: VoteEntity)
    fun setVote(uuid: UUID, serviceName: String, amount: Int)
    fun removeVote(uuid: UUID, serviceName: String, amount: Int)

    fun hasAccount(uuid: UUID): Boolean
    fun hasAccount(uuid: UUID, serviceName: String): Boolean
    fun getPlayerVote(uuid: UUID): Int
    fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity?
    fun removeAccount(uuid: UUID)
    fun resetVotes(uuid: UUID)
    fun resetVotes()
    fun deleteAll()
    fun totalVotes(): Int
    fun topVotes(): MutableMap<String, Int>
}