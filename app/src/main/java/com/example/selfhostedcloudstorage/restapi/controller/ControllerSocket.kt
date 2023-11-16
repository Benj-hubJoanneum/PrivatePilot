package com.example.selfhostedcloudstorage.restapi.controller

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.restapi.client.WebSocketClient
import com.example.selfhostedcloudstorage.restapi.model.MetadataResponse
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository
import com.google.gson.Gson
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ControllerSocket(private val nodeRepository: NodeRepository, private val callback: ControllerCallback) :
    WebSocketClient.WebSocketCallback {
    private val webSocketClient: WebSocketClient = WebSocketClient(this)
    private var context: Context? = null

    fun createNodes(url: String, file: File) {
        val filepath = "$url/${file.name}"
        sendToServer("POST", filepath) // send request to mkdir of filepath
        val byteString = file.parseFileToBytes()
        if (byteString != null) sendToServer(byteString) // send file to server
    }

    fun requestNodes(url: String) {
        sendToServer("GET", url)
    }

    private fun readNodes(json: String) {
        try {
            val directoryList = mutableSetOf<DirectoryItem>()
            val nodeList: MutableSet<INode>
            val data = json.parseItemsFromResponse()

            directoryList.addAll(data.items.filter { it.type == "folder" }.map {
                DirectoryItem(it.name,"/${it.path}")
            })
            nodeList = data.items.map { NodeItem(it.name, "/${it.path}") }.toMutableSet()

            callback.onControllerSourceChanged(directoryList, nodeList)
        } catch (e: IOException) {
            println("Error parsing JSON: ${e.message}")
        }
    }

    fun updateNodes(url: String) {
        // TODO: check if a namechange of exchange the whole file
        /*sendToServer("UPDATE", url)*/
    }

    fun deleteNodes(url: String) {
        sendToServer("DELETE", url)
    }

    fun downloadFile(url: String) {
        sendToServer("GET", url)
    }

    fun fileExist(url: String, context: Context, write: Boolean = false): File {
        val fileName = url.substringAfterLast('/')
        val filepath = url.substringBeforeLast('/')
        val dirPath = "public_$filepath"

        this.context = context

        val file = context.getExternalFilesDir(dirPath)

        if (write)
            file?.mkdirs()

        return File(file, fileName)
    }

    fun openFile(url: String) {
        if (context != null) {
            val file = fileExist(url, context!!)

            if (file.exists()) {
                val mimeType = context!!.contentResolver.getType(Uri.fromFile(file))
                val intent = Intent(Intent.ACTION_VIEW)
                val uri = FileProvider.getUriForFile(context!!, context!!.applicationContext.packageName + ".provider", file)

                intent.setDataAndType(uri, mimeType)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                try {
                    context!!.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun sendSearchRequest(query: String) {
        sendToServer("SEARCH", query)
    }

    private fun convertFileToJson(file: File): String {
        val gson = Gson()
        return gson.toJson(file)
    }

    private fun convertInputStreamToString(inputStream: InputStream?): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        return stringBuilder.toString()
    }

    private fun String.parseItemsFromResponse(): MetadataResponse {
        return try {
            Gson().fromJson(this, MetadataResponse::class.java)
        } catch (e: Exception) {
            MetadataResponse(listOf())
        }
    }

    private fun ByteString.parseBytesToFile(file: File) {
        return try {
            val byteArray = this.toByteArray()
            file.writeBytes(byteArray)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun File.parseFileToBytes(): ByteString? {
        return try {
            this.readBytes().toByteString()
        } catch (e: IOException) {
            e.printStackTrace()
            ByteString.EMPTY
        }
    }

    private fun saveFileToPhone(message: ByteString) {
        if (context != null) {
            try {
                if (message.size > 0) {
                    val pointer = nodeRepository.filePointer

                    // create file on phone
                    val outputFile = fileExist(pointer, this.context!!)

                    // write data to file
                    message.parseBytesToFile(outputFile)

                }
            } catch (e: IOException) {
                println("Error saving file: ${e.message}")
            }
        }
    }

    private fun sendToServer(prefix: String, content: String) {
        val conn = webSocketClient.getConnection()
        val requestMessage = "$prefix:$content"
        conn.send(requestMessage)
    }

    private fun sendToServer(requestMessage: ByteString) {
        val conn = webSocketClient.getConnection()
        conn.send(requestMessage)
    }

    override fun onMessageReceived(message: String) {
        readNodes(message)
    }

    override fun onMessageReceived(message: ByteString) {
        saveFileToPhone(message)
    }

    interface ControllerCallback {
        fun onControllerSourceChanged(directoryList : MutableSet<DirectoryItem>, nodeList: MutableSet<INode>)
    }
}
