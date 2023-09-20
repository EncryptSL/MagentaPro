package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.magenta.api.database.DatabaseProvider
import com.github.encryptsl.magenta.common.database.tables.HomeTable
import com.github.encryptsl.magenta.common.database.tables.WarpTable
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector : DatabaseProvider {
    override fun connect(jdbcHost: String, user: String, pass: String) {
        val config = HikariDataSource().apply {
            maximumPoolSize = 10
            jdbcUrl = jdbcHost
            username = user
            password = pass
        }

        Database.connect(config)

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(HomeTable)
            SchemaUtils.create(WarpTable)
        }
    }
}