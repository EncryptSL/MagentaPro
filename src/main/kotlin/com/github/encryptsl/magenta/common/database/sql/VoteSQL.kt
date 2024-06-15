package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import java.util.*
import java.util.concurrent.CompletableFuture

interface VoteSQL {
    fun createAccount(voteImpl: VoteEntity)
    fun getUserVotesByUUIDAndService(uuid: UUID, serviceName: String): CompletableFuture<VoteEntity>
    fun getUserVotesByUUID(uuid: UUID): CompletableFuture<Int>
    fun hasAccount(uuid: UUID): CompletableFuture<Boolean>
    fun hasAccount(uuid: UUID, serviceName: String): CompletableFuture<Boolean>
    fun addVote(voteImpl: VoteEntity)
    fun setVote(uuid: UUID, serviceName: String, amount: Int)
    fun removeVote(uuid: UUID, serviceName: String, amount: Int)
    fun removeAccount(uuid: UUID)
    fun resetVotes(uuid: UUID)
    fun resetVotes()
    fun deleteAll()
    fun totalVotes(): CompletableFuture<Int>
    fun topVotes(): MutableMap<String, Int>
}