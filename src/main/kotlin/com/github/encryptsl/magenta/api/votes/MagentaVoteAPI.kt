package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VoteEntity
import com.github.encryptsl.magenta.common.database.models.VoteModel
import java.util.*

class MagentaVoteAPI() : VoteAPI {


    private val voteModel: VoteModel by lazy { VoteModel() }

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
        return voteModel.hasAccount(uuid).join()
    }
    override fun hasAccount(uuid: UUID, serviceName: String): Boolean {
        return voteModel.hasAccount(uuid, serviceName).join()
    }
    override fun getPlayerVote(uuid: UUID): Int {
        return voteModel.getPlayerVote(uuid).join()
    }

    override fun getPlayerVote(uuid: UUID, serviceName: String): VoteEntity? {
        return voteModel.getPlayerVote(uuid, serviceName).join()
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
        return voteModel.totalVotes().join()
    }

    override fun votesLeaderBoard(): MutableMap<String, Int> {
        return voteModel.topVotes()
    }
}