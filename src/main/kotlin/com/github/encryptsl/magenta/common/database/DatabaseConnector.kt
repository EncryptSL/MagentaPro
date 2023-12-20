package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.tables.*
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector(private val magenta: Magenta) : DatabaseConnectorProvider {
    override fun initConnect(jdbcHost: String, user: String, pass: String) {
        val config = dataSource().apply {
            maximumPoolSize = 10
            jdbcUrl = jdbcHost
            username = user
            password = pass
        }

        magenta.logger.info("Database connecting...")
        Database.connect(config)
        magenta.logger.info("Database successfully connected.")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(HomeTable, WarpTable, VoteTable, LevelTable, VotePartyTable)
        }
    }

    override fun dataSource(): HikariDataSource {
        return HikariDataSource()
    }
}