package com.github.encryptsl.magenta.api.database

interface DatabaseProvider {
    /**
     * This method connecting to database
     * @param jdbcHost - Host of database, or path to file.
     * @param user - User of database
     * @param pass - Password of database
     */
    fun connect(jdbcHost: String, user: String, pass: String)
}