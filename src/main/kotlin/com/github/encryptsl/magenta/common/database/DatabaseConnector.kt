package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.tables.*
import com.maxmind.geoip2.DatabaseReader
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

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

    override fun initGeoMaxMind() {
        if (File(magenta.dataFolder.path, "GeoLite2-Country.mmdb").exists()) return

        magenta.saveResource("GeoLite2-Country.mmdb", false)
        magenta.logger.info("GeoLite2-Country database was initialized !")
    }

    override fun getGeoMaxMing(): DatabaseReader {
        val file = File(magenta.dataFolder.path, "GeoLite2-Country.mmdb")
        return DatabaseReader.Builder(file).build()
    }

    override fun dataSource(): HikariDataSource {
        return HikariDataSource()
    }
}