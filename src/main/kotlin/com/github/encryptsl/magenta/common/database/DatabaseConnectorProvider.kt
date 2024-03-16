package com.github.encryptsl.magenta.common.database

import com.maxmind.geoip2.DatabaseReader
import com.zaxxer.hikari.HikariDataSource

interface DatabaseConnectorProvider {
    /**
     * This method connecting to database
     * @param jdbcHost - Host of database, or path to file.
     * @param user - User of database
     * @param pass - Password of database
     */
    fun initConnect(jdbcHost: String, user: String, pass: String)

    fun initGeoMaxMind(url: String)

    fun getGeoMaxMing(): DatabaseReader

    fun dataSource(): HikariDataSource
}