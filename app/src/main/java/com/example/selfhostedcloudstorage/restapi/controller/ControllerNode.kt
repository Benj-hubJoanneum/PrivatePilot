package com.example.selfhostedcloudstorage.restapi.controller

import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.restapi.client.ClientAccess
import com.example.selfhostedcloudstorage.restapi.client.WebApiClient
import com.example.selfhostedcloudstorage.restapi.model.IMetadata
import com.example.selfhostedcloudstorage.restapi.model.MetadataResponse
import com.google.gson.Gson
import okio.IOException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ControllerNode {
    private val clientAccess: ClientAccess = ClientAccess.getInstance()

    var directoryList = mutableSetOf<DirectoryItem>()
    var _nodeList = mutableSetOf<INode>()

    private var listener: ControllerListener? = null

    suspend fun readNodes(url: String) {
        val webApiClient = WebApiClient()

        webApiClient.requestInputStream(url, object : WebApiClient.WebApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    val json = convertInputStreamToString(inputStream)
                    val data = json.parseItemsFromResponse()

                    directoryList.addAll(data.items.filter { it.type == "folder" }.map {
                        DirectoryItem(it.name, "$url/${it.name}")
                    })
                    _nodeList = data.items.map { NodeItem(it.name, "$url/${it.name}") }.toMutableSet()
                    listener?.onSourceChanged()
                } catch (e: IOException) {
                    println("Error parsing JSON: ${e.message}")
                }
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
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
    fun addListener(controllerListener: ControllerListener) {
        listener = controllerListener
    }
}
