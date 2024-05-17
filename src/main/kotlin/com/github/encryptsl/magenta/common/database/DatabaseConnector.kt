package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.kmono.lib.api.database.DatabaseBuilder
import com.github.encryptsl.kmono.lib.api.database.DatabaseConnectorProvider
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.tables.*
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector(private val magenta: Magenta) : DatabaseConnectorProvider {
    override fun initConnect(jdbcHost: String, user: String, pass: String) {
        DatabaseBuilder.Builder()
            .setJdbc(jdbcHost)
            .setUser(user)
            .setPassword(pass)
            .setConnectionPool(10)
            .setLogger(magenta.slF4JLogger)
            .setDatasource(dataSource())
            .connect()

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(HomeTable, WarpTable, VoteTable, LevelTable, VotePartyTable)
        }
    }

    override fun dataSource(): HikariDataSource {
        return HikariDataSource()
    }
}