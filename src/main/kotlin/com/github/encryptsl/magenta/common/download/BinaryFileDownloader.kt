package com.github.encryptsl.magenta.common.download

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BinaryFileDownloader(private val plugin: Plugin) {

    private val client = OkHttpClient()

    fun downloadFile(url: String, destinationFolder: String, fileName: String, onDownloadComplete: (File) -> Unit) {
        val file = File(destinationFolder, fileName)
        if (file.exists()) {
            onDownloadComplete(file)
            return
        }

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                plugin.logger.info( "Downloading failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    plugin.logger.severe("Downloading failed because of state code: ${response.code}")
                    return
                }

                val inputStream = response.body!!.byteStream()
                val fileOutputStream = FileOutputStream(file)
                try {
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (true) {
                        bytesRead = inputStream.read(buffer)
                        if (bytesRead == -1) break
                        fileOutputStream.write(buffer, 0, bytesRead)
                    }
                    fileOutputStream.flush()
                    onDownloadComplete(file)
                } catch (e: IOException) {
                    plugin.logger.severe("Saving of file failed : ${e.message}")
                } finally {
                    inputStream.close()
                    fileOutputStream.close()
                }
            }
        })
    }

}