package com.example.selfhostedcloudstorage.restapi.controller

import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.restapi.client.WebSocketClient
import com.example.selfhostedcloudstorage.restapi.model.MetadataResponse
import com.google.gson.Gson
import okio.IOException

class ControllerSocket {
    var directoryList = mutableSetOf<DirectoryItem>()
    var _nodeList = mutableSetOf<INode>()

    private var listener: ControllerListener? = null

    private val webSocketClient = WebSocketClient()

    fun connectWebSocket(url: String) {
        /*webSocketClient.connectWebSocket(url, object : WebSocketClient.WebSocketCallback {
            override fun onOpen() {
                // WebSocket connection has been established
            }

            override fun onMessage(message: String) {
                try {
                    val data = message.parseItemsFromResponse()

                    directoryList.addAll(data.items.filter { it.type == "folder" }.map {
                        DirectoryItem(it.name, "$url/${it.name}")
                    })
                    _nodeList = data.items.map { NodeItem(it.name, "$url/${it.name}") }.toMutableSet()
                    listener?.onSourceChanged()
                } catch (e: IOException) {
                    println("Error parsing JSON: ${e.message}")
                }
            }

            override fun onClosing(code: Int, reason: String) {
                // WebSocket is closing
            }

            override fun onClosed(code: Int, reason: String) {
                // WebSocket has been closed
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })*/
    }

    fun sendWebSocketMessage(message: String) {
        webSocketClient.sendWebSocketMessage(message)
    }

    fun closeWebSocket(code: Int, reason: String) {
        webSocketClient.closeWebSocket(code, reason)
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