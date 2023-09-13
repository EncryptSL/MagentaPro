package com.github.encryptsl.magenta.api.database

interface Database {
    /**
     * This method connecting to database
     * @param jdbcHost - Host of database, or path to file.
     * @param driver - Driver of database connection
     * @param user - User of database
     * @param pass - Password of database
     */
    fun connect(jdbcHost: String, driver: String, user: String, pass: String)
}