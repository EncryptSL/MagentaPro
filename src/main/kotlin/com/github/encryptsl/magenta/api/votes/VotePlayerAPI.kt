package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.models.VoteModel
import java.util.UUID

class VotePlayerAPI(private val magenta: Magenta) : VoteAPI {

    private val voteModel: VoteModel by lazy { VoteModel(magenta) }

    override fun addVote(voteImpl: VoteEntity) {
        if (!hasAccount(voteImpl.uuid, voteImpl.serviceName)) {
            voteModel.createAccount(voteImpl)
        } else {
            voteModel.addVote(voteImpl)
        }
    }

    override fun hasAccount(uuid: UUID, serviceName: String): Boolean {
        return voteModel.hasAccount(uuid, serviceName)
    }
    override fun getPlayerVote(uuid: UUID): Int {
        return voteModel.getPlayerVote(uuid)
    }

    override fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity? {
        return voteModel.getPlayerVote(uuid, serviceName)
    }

    override fun getVotesForParty(): Int {
        return voteModel.getVotesForParty()
    }

    override fun removeAccount(uuid: UUID) {
        voteModel.removeAccount(uuid)
    }

    override fun cleanVotes() {
        TODO("Not yet implemented")
    }

    override fun cleanAll() {
        voteModel.cleanAll()
    }

    override fun totalVotes(): Int {
        return voteModel.totalVotes()
    }

    override fun topVotes(): MutableMap<String, Int> {
        return voteModel.topVotes()
    }
}