package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.models.VoteModel
import java.util.*

class MagentaVoteAPI(private val magenta: Magenta, private val voteModel: VoteModel) : VoteAPI {

    override fun addVote(voteImpl: VoteEntity) {
        if (!hasAccount(voteImpl.uuid, voteImpl.serviceName)) {
            voteModel.createAccount(voteImpl)
        } else {
            voteModel.addVote(voteImpl)
        }
    }

    override fun setVote(uuid: UUID, serviceName: String, amount: Int) {
        voteModel.setVote(uuid, serviceName, amount)
    }

    override fun removeVote(uuid: UUID, serviceName: String, amount: Int) {
        voteModel.removeVote(uuid, serviceName, amount)
    }
    override fun hasAccount(uuid: UUID): Boolean {
        return voteModel.hasAccount(uuid)
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
        return magenta.config.getInt("votifier.voteparty.current_votes")
    }

    override fun removeAccount(uuid: UUID) {
        voteModel.removeAccount(uuid)
    }

    override fun resetVotes(uuid: UUID) {
        voteModel.resetVotes(uuid)
    }

    override fun resetVotes() {
        voteModel.resetVotes()
    }

    override fun deleteAll() {
        voteModel.deleteAll()
    }

    override fun totalVotes(): Int {
        return voteModel.totalVotes()
    }

    override fun topVotes(): MutableMap<String, Int> {
        return voteModel.topVotes()
    }
}