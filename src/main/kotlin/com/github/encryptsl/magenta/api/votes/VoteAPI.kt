package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import java.util.*
import java.util.concurrent.CompletableFuture

interface VoteAPI {
    fun addVote(voteImpl: VoteEntity)
    fun setVote(uuid: UUID, serviceName: String, amount: Int)
    fun removeVote(uuid: UUID, serviceName: String, amount: Int)

    fun hasAccount(uuid: UUID): Boolean
    fun hasAccount(uuid: UUID, serviceName: String): Boolean
    fun getUserVotesByUUID(uuid: UUID): CompletableFuture<Int>
    fun getUserVotesByUUIDAndService(uuid: UUID, serviceName: String): CompletableFuture<VoteEntity>
    fun removeAccount(uuid: UUID)
    fun resetVotes(uuid: UUID)
    fun resetVotes()
    fun deleteAll()
    fun totalVotes(): CompletableFuture<Int>
    fun votesLeaderBoard(): Map<String, Int>
}