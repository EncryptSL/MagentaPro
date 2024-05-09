package com.github.encryptsl.magenta.common.database

import com.github.encryptsl.kmono.lib.api.downloader.BinaryFileDownloader
import com.github.encryptsl.magenta.Magenta
import com.maxmind.geoip2.DatabaseReader
import java.io.File
import java.io.IOException

class GeoMaxMindDatabase(private val magenta: Magenta) {

    fun initGeoMaxMind(url: String) {
        val downloader = BinaryFileDownloader(magenta)
        downloader.downloadFile(url,"${magenta.dataFolder}", "GeoLite2-Country.mmdb") { e ->
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
}