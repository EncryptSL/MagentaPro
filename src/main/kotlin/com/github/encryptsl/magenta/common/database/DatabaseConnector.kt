package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.kmono.lib.api.database.DatabaseConnectorProvider
import com.github.encryptsl.kmono.lib.api.downloader.BinaryFileDownloader
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
import java.io.IOException

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

    fun initGeoMaxMind(url: String) {
        BinaryFileDownloader(magenta).downloadFile(url,"${magenta.dataFolder}", "GeoLite2-Country.mmdb") { e ->
            magenta.logger.info("${e.name} database was found !")
        }
    }

    fun getGeoMaxMing(): DatabaseReader
    {
        var db: DatabaseReader? = null
        try {
            val file = File(magenta.dataFolder.path, "GeoLite2-Country.mmdb")
            db = DatabaseReader.Builder(file).build()
            return db
        } catch (e : IOException) {
            db?.close()
            throw Exception(e.message ?: e.localizedMessage)
        }
    }

    override fun dataSource(): HikariDataSource {
        return HikariDataSource()
    }
}